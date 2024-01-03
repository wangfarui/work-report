package approval.dingtalk.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 钉钉回调变更内容
 *
 * @author wangfarui
 * @since 2023/8/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DingTalkCallbackChange extends DingTalkCallbackEvent {

    private JSONObject data;
}
