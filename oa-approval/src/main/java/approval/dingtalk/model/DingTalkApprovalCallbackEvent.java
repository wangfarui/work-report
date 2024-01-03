package approval.dingtalk.model;

import com.wyyt.scp.biz.approval.ApprovalEventType;
import com.wyyt.scp.biz.approval.BusinessApprovalCallbackEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批回调事件对象
 *
 * @author wangfarui
 * @since 2023/8/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DingTalkApprovalCallbackEvent extends BusinessApprovalCallbackEvent {

    /**
     * 审批事件类型
     */
    private ApprovalEventType approvalEventType;

}
