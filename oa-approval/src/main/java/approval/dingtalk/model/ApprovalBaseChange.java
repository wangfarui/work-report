package approval.dingtalk.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批变更事件的基础推送数据
 *
 * @author wangfarui
 * @since 2023/8/23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApprovalBaseChange extends DingTalkCallbackChange {

    /**
     * 审批实例id。
     */
    private String processInstanceId;

    /**
     * 结束审批实例时间。时间戳，单位毫秒。
     */
    private Long finishTime;

    /**
     * 创建审批实例时间。时间戳，单位毫秒。
     */
    private Long createTime;

    /**
     * 审批模板的唯一码。
     */
    private String processCode;

    /**
     * 业务分类标识。
     */
    private String bizCategoryId;

    /**
     * 流程实例业务标识。
     */
    private String businessId;

    /**
     * 审批实例标题。
     */
    private String title;

    /**
     * 发起审批实例的员工userId。
     */
    private String staffId;

    private String staffName;

}
