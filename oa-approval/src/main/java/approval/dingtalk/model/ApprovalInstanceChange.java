package approval.dingtalk.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批实例状态变更事件的推送数据
 *
 * @author wangfarui
 * @since 2023/8/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApprovalInstanceChange extends ApprovalBaseChange {

    /**
     * 实例状态变更类型：
     * start：审批实例开始
     * finish：审批正常结束（同意或拒绝）
     * terminate：审批终止（发起人撤销审批单）
     */
    private String type;

    /**
     * 业务身份。
     */
    private String businessType;

    /**
     * 审批实例url，可在钉钉内跳转到审批页面。
     */
    private String url;

    /**
     * 审批结果(审批终止时无此参数)：
     * agree： 同意
     * refuse：拒绝
     */
    private String result;

}
