package approval.system.callback;

import lombok.Data;

/**
 * 审批回调选项
 *
 * @author wangfarui
 * @since 2023/2/20
 */
@Data
public class CallbackApprovalOption {

    private String approveAction;

    private String approveComment;

    /**
     * 审批人名称
     */
    private String approverUserName;

    /**
     * 审批人id
     */
    private Long approverUserId;

    private Long approveTime;

}
