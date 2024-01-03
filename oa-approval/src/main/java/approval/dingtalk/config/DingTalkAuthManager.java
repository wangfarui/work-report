package approval.dingtalk.config;

import com.aliyun.dingtalkworkflow_1_0.models.GetAttachmentSpaceResponseBody;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.wyyt.scp.biz.approval.dingtalk.client.DingTalkAccessTokenClient;
import com.wyyt.scp.biz.approval.dingtalk.client.DingTalkUserInfoClient;
import com.wyyt.scp.biz.approval.dingtalk.client.DingTalkWorkflowClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.wyyt.scp.biz.approval.dingtalk.config.DingTalkApprovalConfig.getValueByTimerCache;

/**
 * 钉钉授权信息管理
 *
 * @author wangfarui
 * @since 2023/8/1
 */
@Slf4j
public class DingTalkAuthManager {

    public static final String TOKEN_KEY = "token";

    public static final String DEFAULT_UNION_ID = "defaultUnionId";

    public static final String SPACE_ID = "spaceId";

    public static String getToken() {
        return getValueByTimerCache(TOKEN_KEY, DingTalkAccessTokenClient::getToken);
    }

    public static String getDefaultUnionId() {
        return getValueByTimerCache(DEFAULT_UNION_ID, () -> {
            String userId = DingTalkApprovalConfig.getTenantPropertiesValue(DingTalkTenantProperties::getUserId);
            OapiV2UserGetResponse.UserGetResponse response = DingTalkUserInfoClient.getUserById(userId);
            return response.getUnionid();
        });
    }

    public static String getUnionId(String userId) {
        return getValueByTimerCache(userId, () -> {
            OapiV2UserGetResponse.UserGetResponse response = DingTalkUserInfoClient.getUserById(userId);
            return response.getUnionid();
        });
    }

    public static String getSpaceId() {
        return getValueByTimerCache(SPACE_ID, () -> {
            GetAttachmentSpaceResponseBody.GetAttachmentSpaceResponseBodyResult attachmentSpace = DingTalkWorkflowClient.getAttachmentSpace();
            return Optional.ofNullable(attachmentSpace)
                    .map(t -> t.getSpaceId().toString())
                    .orElse("");
        });
    }
}
