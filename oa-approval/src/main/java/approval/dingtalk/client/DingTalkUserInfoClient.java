package approval.dingtalk.client;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.request.OapiV2UserGetbymobileRequest;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.api.response.OapiV2UserGetbymobileResponse;
import com.taobao.api.ApiException;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkAuthManager;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkURLConstant;
import com.wyyt.scp.biz.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;

/**
 * 钉钉用户Client
 *
 * @author Wray
 * @since 2023/8/1
 */
@Slf4j
public class DingTalkUserInfoClient {

    public static OapiV2UserGetResponse.UserGetResponse getUserById(String userId) {
        OapiV2UserGetRequest request = new OapiV2UserGetRequest();
        request.setUserid(userId);
        DefaultDingTalkClient client = new DefaultDingTalkClient(DingTalkURLConstant.URL_USER_V2_GET);
        try {
            OapiV2UserGetResponse response = client.execute(request, DingTalkAuthManager.getToken());
            if (response == null || !response.isSuccess()) {
                log.error("钉钉api请求错误, req: {}, res: {}", JSON.toJSONString(request), JSON.toJSONString(response));
                throw new IllegalArgumentException();
            }
            return response.getResult();
        } catch (ApiException e) {
            log.error("钉钉api请求异常", e);
            throw new IllegalStateException();
        }
    }

    public static String getUserIdByMobile(String mobile) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingTalkURLConstant.URL_USER_V2_GETBYMOBILE);
            OapiV2UserGetbymobileRequest req = new OapiV2UserGetbymobileRequest();
            req.setMobile(mobile);
            OapiV2UserGetbymobileResponse rsp = client.execute(req, DingTalkAuthManager.getToken());
            if (!rsp.isSuccess()) {
                throw new BizException("手机号为【" + mobile + "】的用户不存在于钉钉企业，请联系企业管理员加入钉钉企业！");
            }
            return rsp.getResult().getUserid();
        } catch (ApiException e) {
            log.error("钉钉api请求异常", e);
            throw new IllegalStateException();
        }
    }

}
