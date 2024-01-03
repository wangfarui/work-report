package approval.dingtalk.service;

import com.wyyt.scp.biz.approval.dingtalk.dao.DdApprovalProcessLogAttachmentDao;
import com.wyyt.scp.biz.approval.dingtalk.dao.DdApprovalProcessLogDao;
import com.wyyt.scp.biz.approval.dingtalk.model.ApprovalProcessTypeEnum;
import com.wyyt.scp.biz.approval.dingtalk.model.ApprovalTaskChange;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalProcessLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 钉钉审批流程日志 服务层
 *
 * @author wangfarui
 * @since 2023/8/28
 */
@Service
public class DdApprovalProcessLogService {

    @Resource
    private DdApprovalProcessLogDao ddApprovalProcessLogDao;

    @Resource
    private DdApprovalProcessLogAttachmentDao ddApprovalProcessLogAttachmentDao;

    @Transactional(rollbackFor = Exception.class)
    public void generateLog(ApprovalTaskChange taskChange, Long tenantId, Long businessRelId) {
        Integer count = ddApprovalProcessLogDao.lambdaQuery()
                .eq(DdApprovalProcessLog::getTenantId, tenantId)
                .eq(DdApprovalProcessLog::getEventId, taskChange.getEventId())
                .count();
        if (count > 0) {
            // 已生成日志 ignore
            return;
        }

        DdApprovalProcessLog approvalProcessLog = new DdApprovalProcessLog();
        approvalProcessLog.setTenantId(tenantId);
        approvalProcessLog.setBusinessRelId(businessRelId);
        approvalProcessLog.setEventId(taskChange.getEventId());
        approvalProcessLog.setTaskId(taskChange.getTaskId());
        approvalProcessLog.setStaffId(taskChange.getStaffId());
        approvalProcessLog.setStaffName(taskChange.getStaffName());
        approvalProcessLog.setProcessContent(taskChange.getContent() != null ? taskChange.getContent() : taskChange.getRemark());
        approvalProcessLog.setCreateTime(taskChange.getEventBornTime());
        ApprovalProcessTypeEnum processType = ApprovalProcessTypeEnum.confirmProcessTypeByTaskChange(taskChange.getType(), taskChange.getResult());
        approvalProcessLog.setProcessType(processType.getCode());
        ddApprovalProcessLogDao.save(approvalProcessLog);
        ddApprovalProcessLogAttachmentDao.saveLogAttachment(taskChange, tenantId, approvalProcessLog.getId());
    }

}
