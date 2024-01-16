package com.wray.work.dingtalk.robot;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 钉钉客户端
 *
 * @author wangfarui
 * @since 2022/10/18
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class DingTalkClient {

    private final DingTalkProperties properties;

    private static boolean CAN_APPLY;

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);

    @SuppressWarnings("all")
    public DingTalkClient(DingTalkProperties properties) {
        this.properties = properties;
        changeCanApply(properties.isEnabled());
    }

    /**
     * 发送钉钉消息
     *
     * @param request 机器人消息对象
     */
    public void send(final DingTalkSendRequest request) {
        send(request, CAN_APPLY);
    }

    /**
     * 发送钉钉消息
     *
     * @param request  机器人消息对象
     * @param canApply 是否需要发送钉钉机器人消息
     */
    public void send(final DingTalkSendRequest request, boolean canApply) {
        if (canApply) {
            if (properties.isAtAll()) {
                request.setAtAll(properties.isAtAll());
            } else if (CollectionUtils.isNotEmpty(properties.getAtMobiles())) {
                request.setAtMobiles(properties.getAtMobiles());
            }

            boolean completed = request.completeRequestParam();
            if (!completed) {
                log.warn("[DingTalkClient][send]钉钉消息请求对象数据异常, request:{}", JSON.toJSONString(request));
            }
            final String requestUrl = properties.getRequestUrl();
            EXECUTOR_SERVICE.execute(() -> {
                HttpResponse httpResponse = HttpUtil.createPost(requestUrl)
                        .body(JSON.toJSONString(request))
                        .charset(StandardCharsets.UTF_8)
                        .execute();
                if (httpResponse == null) {
                    log.warn("[DingTalkClient][send]发送钉钉消息异常, request:{}", JSON.toJSONString(request));
                } else if (!httpResponse.isOk()) {
                    log.warn("[DingTalkClient][send]发送钉钉消息失败, request:{}, response:{}", JSON.toJSONString(request), JSON.toJSONString(httpResponse));
                }
            });
        }
    }

    public static void changeCanApply(boolean canApply) {
        CAN_APPLY = canApply;
    }
}
