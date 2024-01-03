package approval.dingtalk.util;

import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;

/**
 * 钉钉API-Client请求工具
 *
 * @author wangfarui
 * @since 2023/8/1
 */
@Slf4j
public abstract class DingTalkApiClientUtil {

    public static com.aliyun.dingtalkworkflow_1_0.Client createWorkflowClient() {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        try {
            return new com.aliyun.dingtalkworkflow_1_0.Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static com.aliyun.dingtalkstorage_1_0.Client createStorageClient() {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        try {
            return new com.aliyun.dingtalkstorage_1_0.Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
