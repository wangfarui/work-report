package approval.dingtalk.runner;

import com.dingtalk.open.app.api.OpenDingTalkClient;
import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
import com.dingtalk.open.app.api.security.AuthClientCredential;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkApprovalConfig;
import com.wyyt.scp.biz.approval.dingtalk.config.DingTalkTenantProperties;
import com.wyyt.scp.biz.approval.dingtalk.listener.ApprovalCallbackEventListener;
import com.wyyt.scp.biz.approval.dingtalk.service.DdApprovalFormRelService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 默认钉钉应用 Runner
 *
 * <ol>
 *     <li>注册钉钉事件Stream</li>
 * </ol>
 *
 * @author wangfarui
 * @since 2023/8/17
 */
@Slf4j
public class DefaultDingTalkApplicationRunner extends AbstractDingTalkApplicationRunner {

    /**
     * 钉钉客户端
     */
    private static final List<OpenDingTalkClient> CLIENTS = new ArrayList<>();

    public DefaultDingTalkApplicationRunner(DdApprovalFormRelService ddApprovalFormRelService) {
        super(ddApprovalFormRelService);
    }

    @Override
    protected void outerRun() {
        super.outerRun();
        this.registerEventStream();
    }

    /**
     * 注册钉钉事件Stream
     */
    private void registerEventStream() {
        boolean status = true;
        Collection<DingTalkTenantProperties> allUniqueAppKeyTenantProperties = DingTalkApprovalConfig.getUniqueAppKeyTenantProperties();
        List<OpenDingTalkClient> clientList = new ArrayList<>(allUniqueAppKeyTenantProperties.size());
        for (DingTalkTenantProperties properties : allUniqueAppKeyTenantProperties) {
            OpenDingTalkClient dingTalkClient = OpenDingTalkStreamClientBuilder
                    .custom()
                    .credential(new AuthClientCredential(properties.getAppKey(), properties.getAppSecret()))
                    //注册事件监听
                    .registerAllEventListener(new ApprovalCallbackEventListener())
                    .build();
            try {
                dingTalkClient.start();
                clientList.add(dingTalkClient);
            } catch (Exception e) {
                log.error("钉钉AppKey为[" + properties.getAppKey() + "]的Stream Client启用失败", e);
                throw new RuntimeException(e);
            }
        }
        // 关闭已启动的client
        for (OpenDingTalkClient client : CLIENTS) {
            try {
                client.stop();
            } catch (Exception e) {
                status = false;
                log.error("钉钉Client关闭失败", e);
                // ignore
            }
        }
        CLIENTS.clear();
        CLIENTS.addAll(clientList);
        log.info("钉钉Client注册列表: " + allUniqueAppKeyTenantProperties);
        log.info("钉钉Client注册" + (status ? "成功" : "异常"));
    }
}
