package approval;

import lombok.Data;

/**
 * 业务审批回调事件对象
 *
 * @author wangfarui
 * @since 2023/12/13
 */
@Data
public abstract class BusinessApprovalCallbackEvent {

    /**
     * 业务id
     */
    private Long businessId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 用户id
     * <p>钉钉审批回调时: 当钉钉企业用户与SCM企业用户的手机号匹配不到时，可能为空</p>
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;
}
