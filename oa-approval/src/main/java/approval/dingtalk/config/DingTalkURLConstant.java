package approval.dingtalk.config;

/**
 * 钉钉请求接口地址常量类
 */
public abstract class DingTalkURLConstant {
    /**
     * 钉钉网关gettoken地址
     */
    public static final String URL_GET_TOKKEN = "https://oapi.dingtalk.com/gettoken";

    /**
     * V2 根据userid 查询用户详情
     */
    public static final String URL_USER_V2_GET = "https://oapi.dingtalk.com/topapi/v2/user/get";

    /**
     * V2 根据手机号 查询用户id
     */
    public static final String URL_USER_V2_GETBYMOBILE = "https://oapi.dingtalk.com/topapi/v2/user/getbymobile";

    /**
     * 查询钉钉回调推送失败的事件列表
     */
    public static final String URL_CALLBACK_FAILED = "https://oapi.dingtalk.com/call_back/get_call_back_failed_result";

}
