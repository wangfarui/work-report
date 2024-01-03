package approval.system.callback;

import lombok.Data;

import java.util.List;

/**
 * 审批回调 所有选项
 *
 * @author wangfarui
 * @since 2023/2/20
 */
@Data
public class CallbackAllApprovalOption {

    private String nodeName;

    private List<CallbackApprovalOption> approvalOptions;

    private Long nodeId;
}
