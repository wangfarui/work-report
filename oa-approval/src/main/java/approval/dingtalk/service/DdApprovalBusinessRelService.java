package approval.dingtalk.service;

import cn.hutool.extra.spring.SpringUtil;
import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponseBody;
import com.wyyt.scp.biz.api.dto.sys.DdTenantInstanceRelDto;
import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.ApprovalInstanceStatusEnum;
import com.wyyt.scp.biz.approval.dingtalk.client.DingTalkWorkflowClient;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkApprovalConfig;
import com.wyyt.scp.biz.approval.dingtalk.dao.DdApprovalBusinessRelDao;
import com.wyyt.scp.biz.approval.dingtalk.dao.DdApprovalProcessLogAttachmentDao;
import com.wyyt.scp.biz.approval.dingtalk.dao.DdApprovalProcessLogDao;
import com.wyyt.scp.biz.approval.dingtalk.model.ApprovalProcessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkApprovalFormInstance;
import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkApprovalRecordVo;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalBusinessRel;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalFormRel;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalProcessLog;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdUserRel;
import com.wyyt.scp.biz.core.exception.BizException;
import com.wyyt.scp.biz.core.utils.UserUtils;
import com.wyyt.scp.biz.manager.palteform.DdTenantInstanceRelFeignApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 钉钉审批业务关联表 服务实现类
 * </p>
 *
 * @author wangfarui
 * @since 2023-08-02
 */
@Service
@Slf4j
public class DdApprovalBusinessRelService {

    @Resource
    private DdApprovalBusinessRelDao ddApprovalBusinessRelDao;

    @Resource
    private DdTenantInstanceRelFeignApi ddTenantInstanceRelFeignApi;

    @Resource
    private DdApprovalProcessLogDao ddApprovalProcessLogDao;

    @Resource
    private DdApprovalProcessLogAttachmentDao ddApprovalProcessLogAttachmentDao;

    private DdUserRelService ddUserRelService;

    /**
     * 保存钉钉审批实例关联数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveApprovalBusinessRel(DingTalkApprovalFormInstance instance, DdApprovalFormRel ddApprovalFormRel, String instanceId) {
        // 查询审批实例详情
        GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult instanceDetail = DingTalkWorkflowClient.getProcessInstanceById(instanceId);

        // 保存钉钉审批业务关联数据
        DdApprovalBusinessRel ddApprovalBusinessRel = new DdApprovalBusinessRel();
        ddApprovalBusinessRel.setTenantId(instance.getTenantId());
        ddApprovalBusinessRel.setBusinessId(instance.getBusinessId());
        ddApprovalBusinessRel.setInstanceId(instanceId);
        ddApprovalBusinessRel.setInstanceHash(instanceId.hashCode());
        ddApprovalBusinessRel.setInstanceBusinessId(instanceDetail.getBusinessId());
        ddApprovalBusinessRel.setInstanceTitle(instanceDetail.getTitle());
        ddApprovalBusinessRel.setFormRelId(ddApprovalFormRel.getId());
        ApprovalBusinessTypeEnum approvalBusinessTypeEnum = instance.getApprovalForm().getBusinessApprovalTypeEnum();
        ddApprovalBusinessRel.setTypeCode(approvalBusinessTypeEnum.getCode());
        ddApprovalBusinessRel.setTypeName(approvalBusinessTypeEnum.getName());
        ddApprovalBusinessRel.setProcessCode(ddApprovalFormRel.getProcessCode());
        ddApprovalBusinessRel.setAuditStatus(ApprovalInstanceStatusEnum.RUNNING.getCode());
        ddApprovalBusinessRel.setCreateById(instance.getUserId());
        ddApprovalBusinessRel.setCreateTime(new Date());
        log.info("保存钉钉审批业务关联数据: " + ddApprovalBusinessRel);
        ddApprovalBusinessRelDao.save(ddApprovalBusinessRel);

        // 保存租户与钉钉审批实例关联数据
        DdTenantInstanceRelDto ddTenantInstanceRelDto = new DdTenantInstanceRelDto();
        ddTenantInstanceRelDto.setTenantId(ddApprovalBusinessRel.getTenantId());
        ddTenantInstanceRelDto.setInstanceId(ddApprovalBusinessRel.getInstanceId());
        ddTenantInstanceRelDto.setInstanceHash(ddApprovalBusinessRel.getInstanceHash());
        log.info("保存租户与钉钉审批实例关联数据: " + ddTenantInstanceRelDto);
        ddTenantInstanceRelFeignApi.saveTenantInstanceRel(ddTenantInstanceRelDto);
    }

    /**
     * 根据审批实例获取租户id
     */
    public Long getTenantIdByInstance(String instanceId) {
        DdTenantInstanceRelDto ddTenantInstanceRelDto = new DdTenantInstanceRelDto();
        ddTenantInstanceRelDto.setInstanceId(instanceId);
        ddTenantInstanceRelDto.setInstanceHash(instanceId.hashCode());
        return ddTenantInstanceRelFeignApi.getTenantIdByInstance(ddTenantInstanceRelDto);
    }

    /**
     * 根据审批实例获取审批业务关联数据
     */
    public DdApprovalBusinessRel getDdApprovalBusinessRelByInstance(String instanceId) {
        List<DdApprovalBusinessRel> list = ddApprovalBusinessRelDao.lambdaQuery()
                .eq(DdApprovalBusinessRel::getInstanceHash, instanceId.hashCode())
                .list();
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            return ddApprovalBusinessRelDao.lambdaQuery()
                    .in(DdApprovalBusinessRel::getId, list.stream().map(DdApprovalBusinessRel::getId).collect(Collectors.toList()))
                    .eq(DdApprovalBusinessRel::getInstanceId, instanceId)
                    .last("limit 1")
                    .one();
        }
        return list.get(0);
    }

    public List<DdApprovalBusinessRel> getApprovalRecords(List<Long> businessIdList, Integer... auditStatuses) {
        List<Long> idList = businessIdList.stream().filter(id -> !id.equals(0L)).collect(Collectors.toList());
        if (idList.isEmpty()) {
            return Collections.emptyList();
        }
        return ddApprovalBusinessRelDao.lambdaQuery()
                .in(DdApprovalBusinessRel::getBusinessId, businessIdList)
                .in(auditStatuses.length > 0, DdApprovalBusinessRel::getAuditStatus, Arrays.asList(auditStatuses))
                .orderByDesc(DdApprovalBusinessRel::getCreateTime)
                .list();
    }

    public void updateApprovalAuditStatus(Long id, ApprovalInstanceStatusEnum instanceStatusEnum) {
        if (instanceStatusEnum == null) return;
        ddApprovalBusinessRelDao.lambdaUpdate()
                .eq(DdApprovalBusinessRel::getId, id)
                .set(DdApprovalBusinessRel::getAuditStatus, instanceStatusEnum.getCode())
                .set(DdApprovalBusinessRel::getFinishTime, new Date())
                .update();
    }

    public List<DingTalkApprovalRecordVo> queryLogList(Long businessId) {
        List<DdApprovalBusinessRel> approvalRecords = this.getApprovalRecords(Collections.singletonList(businessId));
        List<DingTalkApprovalRecordVo> result = new ArrayList<>(approvalRecords.size());
        for (DdApprovalBusinessRel record : approvalRecords) {
            DingTalkApprovalRecordVo vo = new DingTalkApprovalRecordVo();
            vo.setInstanceNumber(record.getInstanceBusinessId());
            vo.setAuditStatus(ApprovalInstanceStatusEnum.getInstanceStatusRemark(record.getAuditStatus()));
            vo.setLogList(ddApprovalProcessLogDao.getApprovalLogList(record.getId()));
            result.add(vo);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<DingTalkApprovalRecordVo> refreshLog(Long businessId) {
        // 查询所有审批记录
        List<DdApprovalBusinessRel> approvalRecords = this.getApprovalRecords(Collections.singletonList(businessId));

        // 删除已生成的日志
        List<Long> businessRelIdList = approvalRecords.stream().map(DdApprovalBusinessRel::getId).collect(Collectors.toList());
        ddApprovalProcessLogDao.deleteApprovalLog(businessRelIdList);

        Map<String, String> ddUserNameMap = new HashMap<>();
        Long tenantId = UserUtils.getTenantId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

        try {
            DingTalkApprovalConfig.setTenantIdContext(tenantId);
            // 遍历审批记录
            for (DdApprovalBusinessRel ddApprovalBusinessRel : approvalRecords) {
                GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult instanceResponse = DingTalkWorkflowClient.getProcessInstanceById(ddApprovalBusinessRel.getInstanceId());
                for (GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResultOperationRecords records : instanceResponse.getOperationRecords()) {
                    // 保存日志记录
                    DdApprovalProcessLog approvalProcessLog = new DdApprovalProcessLog();
                    approvalProcessLog.setTenantId(tenantId);
                    approvalProcessLog.setBusinessRelId(ddApprovalBusinessRel.getId());
                    approvalProcessLog.setEventId("");
                    approvalProcessLog.setStaffId(records.getUserId());
                    String staffName = ddUserNameMap.computeIfAbsent(records.getUserId(), t -> {
                        DdUserRel ddUserRel = getDdUserRelService().resolveDdUserRel(tenantId, t);
                        return ddUserRel.getDdUserName();
                    });
                    approvalProcessLog.setStaffName(staffName);
                    approvalProcessLog.setProcessContent(records.getRemark());
                    try {
                        approvalProcessLog.setCreateTime(simpleDateFormat.parse(records.getDate()));
                    } catch (ParseException e) {
                        log.error("日期解析异常", e);
                        throw new BizException("日期解析异常");
                    }
                    ApprovalProcessTypeEnum processType = ApprovalProcessTypeEnum.confirmProcessTypeByOperationRecord(records.getType(), records.getResult());
                    approvalProcessLog.setProcessType(processType.getCode());
                    ddApprovalProcessLogDao.save(approvalProcessLog);
                    // 保存日志附件
                    ddApprovalProcessLogAttachmentDao.doSaveLogAttachment(records, tenantId, approvalProcessLog.getId());
                }
            }
        } finally {
            DingTalkApprovalConfig.removeTenantIdContext();
        }

        return this.queryLogList(businessId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void terminateProcessInstance(Long businessId) {
        DdApprovalBusinessRel approvalBusinessRel = ddApprovalBusinessRelDao.lambdaQuery()
                .eq(DdApprovalBusinessRel::getBusinessId, businessId)
                .orderByDesc(DdApprovalBusinessRel::getId)
                .last("limit 1")
                .one();
        if (approvalBusinessRel == null) {
            throw new BizException("未找到对应的钉钉审批流程");
        }
        if (!ApprovalInstanceStatusEnum.RUNNING.getCode().equals(approvalBusinessRel.getAuditStatus())) {
            throw new BizException("审批流程状态不是审批中，无法撤销");
        }
        Date nowDate = new Date();
        long createTime = approvalBusinessRel.getCreateTime().getTime();
        if (nowDate.getTime() - createTime <= 15000) {
            throw new BizException("审批发起15秒内不能撤销审批流程");
        }
        Long tenantId = UserUtils.getTenantId();
        Long userId = UserUtils.getUserId();
        if (!userId.equals(approvalBusinessRel.getCreateById())) {
            throw new BizException("只有发起人可以撤销审批流程");
        }

        try {
            DingTalkApprovalConfig.setTenantIdContext(tenantId);
            DdUserRel ddUserRel = getDdUserRelService().resolveDdUserRel(tenantId, userId);
            DingTalkWorkflowClient.terminateProcessInstance(approvalBusinessRel.getInstanceId(), ddUserRel.getDdUserId());
        } finally {
            DingTalkApprovalConfig.removeTenantIdContext();
        }
    }

    private DdUserRelService getDdUserRelService() {
        if (this.ddUserRelService == null) {
            this.ddUserRelService = SpringUtil.getBean(DdUserRelService.class);
        }
        return this.ddUserRelService;
    }
}
