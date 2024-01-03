package approval.system;

import com.wyyt.scp.biz.approval.ApprovalEventType;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 物易云商审批流程 审批状态枚举
 *
 * @author wangfarui
 * @since 2022/11/11
 */
@Getter
public enum WyysFlowProcessStatusEnum {
    PROCESSING(1, "processing", "审批中", ApprovalEventType.IN_PROGRESS),
    PASSED(2, "passed", "已通过", ApprovalEventType.AGREE),
    REJECTED(3, "rejected", "已拒绝", ApprovalEventType.REJECT),
    REVOKED(4, "revoked", "已撤销", ApprovalEventType.REJECT),
    EXCEPTION(101, "exception", "异常", ApprovalEventType.IN_PROGRESS);

    private final Integer code;

    private final String processCode;

    private final String processName;

    private final ApprovalEventType approvalEventType;

    WyysFlowProcessStatusEnum(Integer code, String processCode, String processName, ApprovalEventType approvalEventType) {
        this.code = code;
        this.processCode = processCode;
        this.processName = processName;
        this.approvalEventType = approvalEventType;
    }

    public static WyysFlowProcessStatusEnum getEnum(String processCode) {
        WyysFlowProcessStatusEnum[] values = values();
        for (WyysFlowProcessStatusEnum statusEnum : values) {
            if (StringUtils.equalsIgnoreCase(statusEnum.processCode, processCode)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 不支持的审批状态
     *
     * @return true -> 不支持
     */
    public boolean unsupportedStatus() {
        return this == EXCEPTION;
    }

    /**
     * 不执行操作的审批状态
     *
     * @return true -> 不执行
     */
    public boolean inExecutionStatus() {
        return this == PROCESSING;
    }

}
