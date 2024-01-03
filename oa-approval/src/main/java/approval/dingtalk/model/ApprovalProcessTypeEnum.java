package approval.dingtalk.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 审批流程类型
 *
 * @author wangfarui
 * @since 2023/8/28
 */
@Getter
@Slf4j
public enum ApprovalProcessTypeEnum {

    UNKOWNN(0, "未知"),
    START_PROCESS(1, "发起流程"),
    AGREE(2, "审批同意"),
    REFUSE(3, "审批拒绝"),
    COMMENT(4, "评论"),
    REDIRECT(5, "审批转交"),
    TERMINATE(6, "审批撤销");

    private final Integer code;

    private final String remark;

    /** result 操作结果 **/
    public static final String RESULT_AGREE = "agree";

    public static final String RESULT_REFUSE = "refuse";

    /** TaskChange type 操作类型 **/
    public static final String RESULT_REDIRECT = "redirect";

    public static final String TYPE_START = "start";

    public static final String TYPE_FINISH = "finish";

    public static final String TYPE_COMMENT = "comment";

    public static final String TYPE_TERMINATE = "terminate";

    /** OperationRecord type 操作类型 **/
    // 正常执行任务
    public static final String EXECUTE_TASK_NORMAL = "EXECUTE_TASK_NORMAL";
    // 转交任务
    public static final String REDIRECT_TASK = "REDIRECT_TASK";
    // 发起流程实例
    public static final String START_PROCESS_INSTANCE = "START_PROCESS_INSTANCE";
    // 终止(撤销)流程实例
    public static final String TERMINATE_PROCESS_INSTANCE = "TERMINATE_PROCESS_INSTANCE";
    // 结束流程实例
    public static final String FINISH_PROCESS_INSTANCE = "FINISH_PROCESS_INSTANCE";
    // 添加评论
    public static final String ADD_REMARK = "ADD_REMARK";
    // 审批退回
    public static final String REDIRECT_PROCESS = "REDIRECT_PROCESS";


    ApprovalProcessTypeEnum(int code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    /**
     * 任务状态变更类型：
     * start：审批任务开始
     * finish：审批任务正常结束（完成或转交） 再根据result判断 agree（同意）、refuse（拒绝）、redirect（转交）
     * cancel：说明当前节点有多个审批人并且是或签，其中一个人执行了审批，其他审批人会推送cancel类型事件。
     * comment：评论
     */
    public static ApprovalProcessTypeEnum confirmProcessTypeByTaskChange(String type, String result) {
        if (TYPE_START.equals(type)) {
            return START_PROCESS;
        }
        if (TYPE_FINISH.equals(type)) {
            if (RESULT_AGREE.equals(result)) {
                return AGREE;
            } else if (RESULT_REFUSE.equals(result)) {
                return REFUSE;
            } else if (RESULT_REDIRECT.equals(result)) {
                return REDIRECT;
            }
        }
        if (TYPE_COMMENT.equals(type)) {
            return COMMENT;
        }

        log.warn("未知的审批流程类型TaskChange, type: {}, result: {}", type, result);
        return UNKOWNN;
    }

    public static ApprovalProcessTypeEnum confirmProcessTypeByOperationRecord(String type, String result) {
        if (START_PROCESS_INSTANCE.equals(type)) {
            return START_PROCESS;
        }
        if (EXECUTE_TASK_NORMAL.equals(type)) {
            if (RESULT_AGREE.equalsIgnoreCase(result)) {
                return AGREE;
            } else if (RESULT_REFUSE.equalsIgnoreCase(result)) {
                return REFUSE;
            }
        }
        if (REDIRECT_TASK.equals(type)) {
            return REDIRECT;
        }
        if (ADD_REMARK.equals(type)) {
            return COMMENT;
        }
        if (TERMINATE_PROCESS_INSTANCE.equals(type)) {
            return TERMINATE;
        }

        log.warn("未知的审批流程类型OperationRecord, type: {}, result: {}", type, result);
        return UNKOWNN;
    }

    public static String getProcessTypeRemark(Integer code) {
        if (code == null) return "";
        for (ApprovalProcessTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getRemark();
            }
        }
        return "";
    }
}
