package approval.dingtalk.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.wyyt.big.platform.client.context.WyysSessionSupport;
import com.wyyt.big.platform.client.servlet.TokenStorageThreadLocal;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.ApprovalEventType;
import com.wyyt.scp.biz.approval.ApprovalInstanceStatusEnum;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkApprovalConfig;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkTenantProperties;
import com.wyyt.scp.biz.approval.dingtalk.model.*;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalBusinessRel;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdUserRel;
import com.wyyt.scp.biz.approval.dingtalk.service.DdApprovalBusinessRelService;
import com.wyyt.scp.biz.approval.dingtalk.service.DdApprovalProcessLogService;
import com.wyyt.scp.biz.approval.dingtalk.service.DdUserRelService;
import com.wyyt.scp.biz.core.cofig.DynamicDataSourceContextHolder;
import com.wyyt.scp.biz.core.utils.DSUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 钉钉审批回调事件监听器
 *
 * @author wangfarui
 * @since 2023/8/21
 */
@Slf4j
public class ApprovalCallbackEventListener extends AbstractDingTalkStreamEventListener {

    private DdApprovalBusinessRelService ddApprovalBusinessRelService;

    private DdUserRelService ddUserRelService;

    private DdApprovalProcessLogService ddApprovalProcessLogService;

    @Override
    public void handleApprovalEvent(DingTalkCallbackChange eventChange) {
        DingTalkApprovalCallbackEvent dingTalkApprovalCallbackEvent = new DingTalkApprovalCallbackEvent();

        ApprovalBaseChange approvalBaseChange;
        ApprovalInstanceStatusEnum instanceStatusEnum = null;
        if (BPMS_TASK_CHANGE.equals(eventChange.getEventType())) {
            dingTalkApprovalCallbackEvent.setApprovalEventType(ApprovalEventType.IN_PROGRESS);
            approvalBaseChange = BeanUtil.copyProperties(eventChange.getData(), ApprovalTaskChange.class);
        } else {
            ApprovalInstanceChange instanceChange = BeanUtil.copyProperties(eventChange.getData(), ApprovalInstanceChange.class);
            approvalBaseChange = instanceChange;

            if (ApprovalProcessTypeEnum.TYPE_FINISH.equals(instanceChange.getType())) {
                if (ApprovalProcessTypeEnum.RESULT_AGREE.equals(instanceChange.getResult())) {
                    instanceStatusEnum = ApprovalInstanceStatusEnum.AGREE;
                    dingTalkApprovalCallbackEvent.setApprovalEventType(ApprovalEventType.AGREE);
                } else if (ApprovalProcessTypeEnum.RESULT_REFUSE.equals(instanceChange.getResult())) {
                    instanceStatusEnum = ApprovalInstanceStatusEnum.REFUSE;
                    dingTalkApprovalCallbackEvent.setApprovalEventType(ApprovalEventType.REJECT);
                } else {
                    log.warn("未知的审批实例结果. instanceChange: " + instanceChange);
                    return;
                }
            } else if (ApprovalProcessTypeEnum.TYPE_TERMINATE.equals(instanceChange.getType())) {
                log.info("发起人撤销审批单. instanceChange: " + instanceChange);
                instanceStatusEnum = ApprovalInstanceStatusEnum.TERMINATE;
                dingTalkApprovalCallbackEvent.setApprovalEventType(ApprovalEventType.REJECT);
            } else {
                log.warn("未知的审批实例状态变更类型. instanceChange: " + instanceChange);
                return;
            }
        }
        // 设置事件基础值
        approvalBaseChange.setEventId(eventChange.getEventId());
        approvalBaseChange.setEventType(eventChange.getEventType());
        approvalBaseChange.setEventBornTime(eventChange.getEventBornTime());

        // 查询审批实例对应的租户id
        Long tenantId = this.getDdApprovalBusinessRelService().getTenantIdByInstance(approvalBaseChange.getProcessInstanceId());
        if (tenantId == null) {
            log.warn("无法确定钉钉审批实例对应的租户信息，请确认审批实例数据来源是否合规.");
            return;
        }

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
            DingTalkApprovalConfig.setTenantIdContext(tenantId);

            // 根据流程实例id 查询审批业务关联单据
            DdApprovalBusinessRel ddApprovalBusinessRel = this.getDdApprovalBusinessRelService().getDdApprovalBusinessRelByInstance(approvalBaseChange.getProcessInstanceId());

            String tenantName = DingTalkApprovalConfig.getTenantPropertiesValue(tenantId, DingTalkTenantProperties::getTenantName);
            if (ddApprovalBusinessRel == null) {
                log.error(String.format("租户[%s]审批实例不存在, instanceId: %s", tenantName, approvalBaseChange.getProcessInstanceId()));
                return;
            }

            // 根据code获取枚举
            ApprovalBusinessTypeEnum businessTypeEnum = ApprovalBusinessTypeEnum.getBusinessTypeEnum(ddApprovalBusinessRel.getTypeCode());
            if (businessTypeEnum == null) {
                log.error(String.format("租户[%s]找不到对应的业务审批类型，请确认是否进行过变更。业务审批关联数据: %s", tenantName, ddApprovalBusinessRel));
                return;
            }

            // 获取审批人
            DdUserRel ddUserRel = getDdUserRelService().resolveDdUserRel(tenantId, approvalBaseChange.getStaffId());
            if (ddUserRel == null) {
                log.warn(String.format("租户[%s]无法找到可以关联的用户，钉钉用户id为[%s]", tenantName, approvalBaseChange.getStaffId()));
            } else {
                dingTalkApprovalCallbackEvent.setUserId(ddUserRel.getScmUserId());
                approvalBaseChange.setStaffName(ddUserRel.getDdUserName());
            }

            // 根据审批任务事件生成审批流程日志
            if (approvalBaseChange instanceof ApprovalTaskChange) {
                this.getDdApprovalProcessLogService().generateLog((ApprovalTaskChange) approvalBaseChange, tenantId, ddApprovalBusinessRel.getId());
            }
            // 根据审批实例事件更新审批状态
            else {
                this.getDdApprovalBusinessRelService().updateApprovalAuditStatus(ddApprovalBusinessRel.getId(), instanceStatusEnum);
            }

            // 根据枚举+钉钉事件类型 调用监听的方法
            List<DingTalkCallbackListener> listeners = DingTalkApprovalConfig.getCallbackListener(businessTypeEnum, dingTalkApprovalCallbackEvent.getApprovalEventType());
            if (listeners == null) {
                log.warn(String.format("租户[%s]未配置审批类型为[%s],事件类型为[%s]的回调监听器, 跳过钉钉回调事件", tenantName, businessTypeEnum.getName(), dingTalkApprovalCallbackEvent.getApprovalEventType().name()));
                return;
            }

            // 填充回调事件对象数据
            dingTalkApprovalCallbackEvent.setTenantId(tenantId);
            dingTalkApprovalCallbackEvent.setBusinessId(ddApprovalBusinessRel.getBusinessId());

            // 业务回调处理
            for (DingTalkCallbackListener listener : listeners) {
                listener.callback(dingTalkApprovalCallbackEvent);
            }

            log.info(String.format("租户[%s]处理钉钉回调事件完成, 回调事件对象: %s", tenantName, dingTalkApprovalCallbackEvent));
        } finally {
            TokenStorageThreadLocal.destory();
            DingTalkApprovalConfig.removeTenantIdContext();
        }
    }

    private DdApprovalProcessLogService getDdApprovalProcessLogService() {
        if (this.ddApprovalProcessLogService == null) {
            this.ddApprovalProcessLogService = SpringUtil.getBean(DdApprovalProcessLogService.class);
        }
        return this.ddApprovalProcessLogService;
    }

    private DdApprovalBusinessRelService getDdApprovalBusinessRelService() {
        if (this.ddApprovalBusinessRelService == null) {
            this.ddApprovalBusinessRelService = SpringUtil.getBean(DdApprovalBusinessRelService.class);
        }
        return this.ddApprovalBusinessRelService;
    }

    private DdUserRelService getDdUserRelService() {
        if (this.ddUserRelService == null) {
            this.ddUserRelService = SpringUtil.getBean(DdUserRelService.class);
        }
        return this.ddUserRelService;
    }
}
