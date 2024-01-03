package approval.dingtalk.config;

import lombok.Data;

/**
 * 钉钉-租户 信息
 *
 * @author wangfarui
 * @since 2023/8/17
 */
@Data
public class DingTalkTenantProperties {

    /**
     * 租户名称（用于展示）
     */
    private String tenantName;

    /**
     * 应用凭证 - AppKey
     */
    private String appKey;

    /**
     * 应用凭证 - AppSecret
     */
    private String appSecret;

    /**
     * 应用凭证 - AgentId
     */
    private Long agentId;

    /**
     * 管理员的用户id
     * <p>用于开启钉盘存储权限</p>
     */
    private String userId;
}
