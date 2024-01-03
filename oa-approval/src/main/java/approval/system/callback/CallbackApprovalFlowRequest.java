package approval.system.callback;

import lombok.Data;

import java.util.List;

/**
 * 审批流程实例回调请求对象
 *
 * @author wangfarui
 * @since 2022/11/11
 */
@Data
public class CallbackApprovalFlowRequest {

    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 审批流水号
     */
    private String flowNo;
    /**
     * 审批处理状态：PASSED-已通过，REJECTED-已拒绝，REVOKED->已撤销"
     */
    private String flowProcessStatus;
    /**
     * 审批处理状态文本
     */
    private String flowProcessStatusText;

    /**
     * 所有审批项
     */
    private List<CallbackAllApprovalOption> allApprovalOptions;

}
