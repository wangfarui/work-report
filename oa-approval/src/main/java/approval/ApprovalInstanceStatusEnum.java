package approval;

import lombok.Getter;

/**
 * 审批实例状态枚举
 *
 * @author wangfarui
 * @since 2023/9/12
 */
@Getter
public enum ApprovalInstanceStatusEnum {

    RUNNING(1, "审批中"),
    AGREE(2, "已通过"),
    REFUSE(3, "已拒绝"),
    TERMINATE(4, "已撤销");

    private final Integer code;

    private final String remark;

    ApprovalInstanceStatusEnum(Integer code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    public static String getInstanceStatusRemark(Integer code) {
        if (code == null) return "";
        for (ApprovalInstanceStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum.getRemark();
            }
        }
        return "";
    }
}
