package approval.dingtalk.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批任务状态变更事件的推送数据
 *
 * @author wangfarui
 * @since 2023/8/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApprovalTaskChange extends ApprovalBaseChange {

    /**
     * 评论时写的评论内容。
     */
    private String content;

    /**
     * 审批操作写的评论内容
     */
    private String remark;

    /**
     * 任务状态变更类型：
     * start：审批任务开始
     * finish：审批任务正常结束（完成或转交） 再根据result判断 agree（同意）、refuse（拒绝）、redirect（转交）
     * cancel：说明当前节点有多个审批人并且是或签，其中一个人执行了审批，其他审批人会推送cancel类型事件。
     * comment：评论
     */
    private String type;

    /**
     * 任务id。
     */
    private Long taskId;

    /**
     * 审批结果：
     * agree：同意
     * refuse：拒绝
     * redirect：表示审批任务转交
     */
    private String result;

}
