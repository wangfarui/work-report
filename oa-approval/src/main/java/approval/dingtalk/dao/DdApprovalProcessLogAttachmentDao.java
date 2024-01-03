package approval.dingtalk.dao;

import com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceResponseBody;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyyt.scp.biz.approval.dingtalk.client.DingTalkStorageClient;
import com.wyyt.scp.biz.approval.dingtalk.client.DingTalkWorkflowClient;
import com.wyyt.scp.biz.approval.dingtalk.mapper.DdApprovalProcessLogAttachmentMapper;
import com.wyyt.scp.biz.approval.dingtalk.model.ApprovalTaskChange;
import com.wyyt.scp.biz.approval.dingtalk.model.entity.DdApprovalProcessLogAttachment;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DdApprovalProcessLogAttachmentDao
 *
 * @author wangfarui
 * @since 2023/8/28
 */
@Component
public class DdApprovalProcessLogAttachmentDao extends ServiceImpl<DdApprovalProcessLogAttachmentMapper, DdApprovalProcessLogAttachment> {

    public void saveLogAttachment(ApprovalTaskChange taskChange, Long tenantId, Long logId) {
        // 查询实例详情
        GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult instanceResponse = DingTalkWorkflowClient.getProcessInstanceById(taskChange.getProcessInstanceId());
        List<GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResultOperationRecords> operationRecords = instanceResponse.getOperationRecords();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        String eventBornTime = simpleDateFormat.format(taskChange.getEventBornTime());
        for (int len = operationRecords.size() - 1; len >= 0; len--) {
            GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResultOperationRecords records = operationRecords.get(len);
            if (!taskChange.getStaffId().equals(records.getUserId())) {
                continue;
            }
            if (!eventBornTime.equals(records.getDate())) {
                try {
                    Date parse = simpleDateFormat.parse(records.getDate());
                    long after = parse.getTime() + (60 * 1000);
                    if (after < taskChange.getEventBornTime().getTime()) {
                        // 操作记录的时间点 小于 事件发生的事件点
                        break;
                    }
                } catch (ParseException e) {
                    // ignore
                }
                continue;
            }
            this.doSaveLogAttachment(records, tenantId, logId);
            break;
        }
    }

    public void doSaveLogAttachment(GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResultOperationRecords records,
                                    Long tenantId, Long logId) {
        // 匹配到审批操作记录，判断当前审批操作是否携带附件
        if (CollectionUtils.isNotEmpty(records.getAttachments())) {
            List<String> fileIdList = records.getAttachments()
                    .stream()
                    .map(GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResultOperationRecordsAttachments::getFileId)
                    .collect(Collectors.toList());

            if (DingTalkWorkflowClient.authorizeApprovalDentry(fileIdList, records.getUserId())) {
                for (GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResultOperationRecordsAttachments attachment : records.getAttachments()) {
                    String wyysFileUrl = DingTalkStorageClient.uploadFileToWyys(attachment.getFileId(), attachment.getFileName(), records.getUserId());
                    if (wyysFileUrl == null) continue;
                    DdApprovalProcessLogAttachment logAttachment = new DdApprovalProcessLogAttachment();
                    logAttachment.setTenantId(tenantId);
                    logAttachment.setLogId(logId);
                    logAttachment.setFileName(attachment.getFileName());
                    logAttachment.setFileType(attachment.getFileType());
                    logAttachment.setFileUrl(wyysFileUrl);
                    this.save(logAttachment);
                }
            }
        }
    }
}
