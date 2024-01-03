package approval;

/**
 * 审批事件类型
 *
 * @author wangfarui
 * @since 2023/8/16
 */
public enum ApprovalEventType {

    /**
     * 所有审批事件
     */
    ALL,

    /**
     * 审批 - 同意
     */
    AGREE,

    /**
     * 审批 - 拒绝
     */
    REJECT,

    /**
     * 审批 - 进行中
     */
    IN_PROGRESS

}
