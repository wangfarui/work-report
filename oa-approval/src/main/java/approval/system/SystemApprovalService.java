package approval.system;

import com.alibaba.fastjson.JSON;
import com.wyyt.big.platform.client.context.WyysSessionSupport;
import com.wyyt.big.platform.client.servlet.TokenStorageThreadLocal;
import com.wyyt.open.platform.client.module.wyys.flow.FlowOpenApi;
import com.wyyt.open.platform.client.module.wyys.flow.request.FlowInstanceCreateByNewRequest;
import com.wyyt.open.platform.client.module.wyys.flow.request.FlowUpdateTimeRequest;
import com.wyyt.open.platform.client.module.wyys.flow.request.TenantDefineDetailRequest;
import com.wyyt.open.platform.client.module.wyys.flow.response.FlowInstanceCreateResponse;
import com.wyyt.open.platform.client.module.wyys.flow.response.TenantDefineDetailResponse;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.ApprovalInstanceStatusEnum;
import com.wyyt.scp.biz.approval.system.callback.*;
import com.wyyt.scp.biz.core.cofig.DynamicDataSourceContextHolder;
import com.wyyt.scp.biz.core.exception.BizException;
import com.wyyt.scp.biz.core.utils.DSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 系统审批业务关联表服务
 *
 * @author wangfarui
 * @since 2022-11-09
 */
@Service
@Slf4j
public class SystemApprovalService {

    @Resource
    private SystemApprovalBusinessRelDao systemApprovalBusinessRelDao;

    @Value("${scm.request.domain}")
    private String gatewayHost;

    private static final String APPROVAL_CALLBACK_URL = "/scp-biz/approval/system/callbackApprovalFlow";

    /**
     * 发起系统审批流程
     *
     * @param formInstance 创建审批流程的实例对象数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void startApprovalFlowInstance(SystemApprovalFormInstance formInstance) {
        log.info("[SystemApprovalBusinessRelService][startApprovalFlowInstance]输入对象instanceDto: {}", formInstance);
        formInstance.checkParamValid();

        // 校验该审批类型下的业务id是否已存在
        Integer count = systemApprovalBusinessRelDao.lambdaQuery()
                .eq(SystemApprovalBusinessRel::getTenantId, formInstance.getTenantId())
                .eq(SystemApprovalBusinessRel::getBusinessId, formInstance.getBusinessId())
                .eq(SystemApprovalBusinessRel::getAuditStatus, ApprovalInstanceStatusEnum.RUNNING.getCode())
                .count();
        if (count > 0) {
            throw new BizException("已发起系统审批流程");
        }

        // 获取审批表单模板编码
        String templateCode = SystemApprovalConfig.getTemplateCode(formInstance.getBusinessTypeEnum().getCode());

        // 校验审批流表单模板是否存在
        TenantDefineDetailRequest defineDetailRequest = new TenantDefineDetailRequest();
        defineDetailRequest.setTenantId(formInstance.getTenantId());
        defineDetailRequest.setTemplateCode(templateCode);
        log.info("[SystemApprovalBusinessRelService][startApprovalFlowInstance]校验审批流表单模板是否存在, defineDetailRequest: {}", JSON.toJSONString(defineDetailRequest));
        TenantDefineDetailResponse defineDetailResponse = FlowOpenApi.detailTenantDefine(defineDetailRequest);
        log.info("[SystemApprovalBusinessRelService][startApprovalFlowInstance]校验审批流表单模板是否存在, detailResponseRes: {}", JSON.toJSONString(defineDetailResponse));

        // 发起创建流程实例表单
        FlowInstanceCreateByNewRequest createByNewRequest = new FlowInstanceCreateByNewRequest();
        createByNewRequest.setTenantId(formInstance.getTenantId());
        createByNewRequest.setUserId(formInstance.getUserId());
        createByNewRequest.setDepartmentId(formInstance.getDepartmentId());
        createByNewRequest.setTemplateCode(templateCode);
        createByNewRequest.setCallbackUrl(this.gatewayHost + APPROVAL_CALLBACK_URL);
        createByNewRequest.setFormControlValues(formInstance.getApprovalForm().buildFormControlValues());
        log.info("[SystemApprovalBusinessRelService][startApprovalFlowInstance]发起创建流程实例表单, createByNewRequest: {}", JSON.toJSONString(createByNewRequest));
        FlowInstanceCreateResponse flowInstanceRes = FlowOpenApi.createByNew(createByNewRequest);
        log.info("[SystemApprovalBusinessRelService][startApprovalFlowInstance]发起创建流程实例表单, flowInstanceRes: {}", JSON.toJSONString(flowInstanceRes));

        // 保存审批流程实例
        SystemApprovalBusinessRel approvalBusinessRel = new SystemApprovalBusinessRel();
        approvalBusinessRel.setTenantId(formInstance.getTenantId());
        approvalBusinessRel.setBusinessId(formInstance.getBusinessId());
        approvalBusinessRel.setTypeCode(formInstance.getBusinessTypeEnum().getCode());
        approvalBusinessRel.setTypeName(formInstance.getBusinessTypeEnum().getName());
        approvalBusinessRel.setTemplateCode(templateCode);
        approvalBusinessRel.setFlowId(flowInstanceRes.getInstanceId());
        approvalBusinessRel.setFlowSn(flowInstanceRes.getFlowNo());
        approvalBusinessRel.setAuditStatus(ApprovalInstanceStatusEnum.RUNNING.getCode());
        approvalBusinessRel.setBusinessDate(formInstance.getBusinessDate());
        approvalBusinessRel.setCreateById(formInstance.getUserId());
        approvalBusinessRel.setCreateTime(new Date());
        systemApprovalBusinessRelDao.save(approvalBusinessRel);
    }

    /**
     * 回调审批流程实例
     *
     * @param request 审批流程实例回调请求对象
     */
    @Transactional(rollbackFor = Exception.class)
    public void callbackApprovalFlow(CallbackApprovalFlowRequest request) {
        Long tenantId = request.getTenantId();
        // 手动切换数据源
        String dsId = "ds_".concat(tenantId.toString());
        if (DynamicDataSourceContextHolder.containsDataSource(dsId)) {
            DSUtils.ds(tenantId.toString());
        } else {
            log.error("未知的租户id，请确认审批实例数据来源是否合规. tenantId: " + tenantId);
            return;
        }
        try {
            // 上下文配置租户信息
            WyysSessionSupport.initTenantId(tenantId);

            // 校验该审批类型下的业务id是否已存在
            SystemApprovalBusinessRel approvalBusinessRel = systemApprovalBusinessRelDao.lambdaQuery()
                    .eq(SystemApprovalBusinessRel::getTenantId, tenantId)
                    .eq(SystemApprovalBusinessRel::getFlowSn, request.getFlowNo())
                    .last("limit 1")
                    .one();
            if (approvalBusinessRel == null) {
                log.error("[SystemApprovalBusinessRelService][callbackApprovalFlow]回调未找到对应审批流程实例, request: {}", request);
                throw new BizException("对应审批流程不存在");
            }
            if (!ApprovalInstanceStatusEnum.RUNNING.getCode().equals(approvalBusinessRel.getAuditStatus())) {
                log.error("[SystemApprovalBusinessRelService][callbackApprovalFlow]审批流程已经完结, request: {}", request);
                throw new BizException("流程已经完结，禁止重复操作！");
            }

            // 校验审批状态类型
            WyysFlowProcessStatusEnum processStatusEnum = WyysFlowProcessStatusEnum.getEnum(request.getFlowProcessStatus());
            if (processStatusEnum == null) {
                log.error("[SystemApprovalBusinessRelService][callbackApprovalFlow]无法解析的审批状态类型, request: {}", request);
                throw new BizException("更新失败，无法解析未知的审批状态");
            } else if (processStatusEnum.unsupportedStatus()) {
                log.warn("[SystemApprovalBusinessRelService][callbackApprovalFlow]不支持的审批状态类型, request: {}", request);
                return;
            } else if (processStatusEnum.inExecutionStatus()) {
                // 不执行操作的审批状态，直接忽略即可
                return;
            }

            ApprovalBusinessTypeEnum businessTypeEnum = ApprovalBusinessTypeEnum.getBusinessTypeEnum(approvalBusinessRel.getTypeCode());
            if (businessTypeEnum == null) {
                log.error(String.format("租户[%d]找不到对应的业务审批类型，请确认是否进行过变更。业务审批关联数据: %s", tenantId, approvalBusinessRel));
                return;
            }

            // 更新审批流程状态
            systemApprovalBusinessRelDao.lambdaUpdate()
                    .eq(SystemApprovalBusinessRel::getId, approvalBusinessRel.getId())
                    .set(SystemApprovalBusinessRel::getAuditStatus, processStatusEnum.getCode())
                    .set(SystemApprovalBusinessRel::getFinishTime, new Date())
                    .update();

            // 修改审批时间
            if (approvalBusinessRel.getBusinessDate() != null) {
                FlowUpdateTimeRequest flowUpdateTimeRequest = new FlowUpdateTimeRequest();
                flowUpdateTimeRequest.setTenantId(approvalBusinessRel.getTenantId());
                flowUpdateTimeRequest.setUserId(approvalBusinessRel.getCreateById());
                flowUpdateTimeRequest.setTime(approvalBusinessRel.getBusinessDate().getTime());
                flowUpdateTimeRequest.setInstanceId(approvalBusinessRel.getFlowId());
                flowUpdateTimeRequest.setThrowException(false);
                log.info("[SystemApprovalBusinessRelService][callbackApprovalFlow]修改审批时间, flowUpdateTimeRequest: {}", flowUpdateTimeRequest);
                try {
                    boolean res = FlowOpenApi.updateFlowTime(flowUpdateTimeRequest);
                    log.info("[SystemApprovalBusinessRelService][callbackApprovalFlow]修改审批时间, res: {}", res);
                } catch (Throwable e) {
                    log.error("[SystemApprovalBusinessRelService][callbackApprovalFlow]修改审批时间, 异常: " + JSON.toJSONString(flowUpdateTimeRequest), e);
                }
            }

            // 获取审批事件对应的回调监听器
            List<SystemCallbackListener> callbackListeners = SystemApprovalConfig.getCallbackListener(businessTypeEnum, processStatusEnum.getApprovalEventType());
            if (callbackListeners == null) {
                log.warn(String.format("租户[%d]未配置审批表单模板为[%s],事件类型为[%s]的回调监听器, 跳过系统审批回调事件", tenantId, businessTypeEnum.getName(), processStatusEnum.getApprovalEventType().name()));
                return;
            }

            // 定义回调事件对象
            SystemApprovalCallbackEvent callbackEvent = new SystemApprovalCallbackEvent();
            callbackEvent.setTenantId(tenantId);
            callbackEvent.setBusinessId(approvalBusinessRel.getBusinessId());
            CallbackAllApprovalOption lastAllApprovalOption = request.getAllApprovalOptions().get(request.getAllApprovalOptions().size() - 1);
            Optional<CallbackApprovalOption> callbackApprovalOption = lastAllApprovalOption.getApprovalOptions()
                    .stream()
                    .filter(t -> t.getApproveTime() != null)
                    .max((o1, o2) -> (int) (o1.getApproveTime() - o2.getApproveTime()));
            if (callbackApprovalOption.isPresent()) {
                CallbackApprovalOption approvalOption = callbackApprovalOption.get();
                callbackEvent.setUserId(approvalOption.getApproverUserId());
                callbackEvent.setUserName(approvalOption.getApproverUserName());
            }

            // 回调业务审批
            try {
                for (SystemCallbackListener callbackListener : callbackListeners) {
                    callbackListener.callback(callbackEvent);
                }
            } catch (Throwable e) {
                log.error(String.format("租户[%d]对应审批表单模板为[%s],事件类型为[%s]的回调监听器, 调用失败!", tenantId, businessTypeEnum.getName(), processStatusEnum.getApprovalEventType().name()), e);
            }

            log.info(String.format("租户[%d]处理系统审批回调事件完成, 回调事件对象: %s", tenantId, JSON.toJSONString(callbackEvent)));
        } finally {
            TokenStorageThreadLocal.destory();
        }
    }
}
