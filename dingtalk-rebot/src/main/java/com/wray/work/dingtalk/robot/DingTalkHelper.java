package com.wray.work.dingtalk.robot;

import com.wyyt.ebc.common.utils.ApplicationContextUtils;
import com.wyyt.ebc.common.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * 钉钉辅助器
 *
 * @author wangfarui
 * @since 2022/10/18
 */
@Slf4j
public abstract class DingTalkHelper {

    private static volatile DingTalkClient DING_TALK_CLIENT;

    private static DingTalkClient getDingTalkClient() {
        if (DING_TALK_CLIENT == null) {
            synchronized (DingTalkHelper.class) {
                if (DING_TALK_CLIENT == null) {
                    ApplicationContext applicationContext = ApplicationContextUtils.getApplicationContext();
                    try {
                        DING_TALK_CLIENT = applicationContext.getBean(DingTalkClient.class);
                    } catch (BeansException e) {
                        log.error("获取DingTalkClient Bean异常", e);
                        throw new IllegalStateException("钉钉客户端加载异常");
                    }
                }
            }
        }
        return DING_TALK_CLIENT;
    }

    public static void send(DingTalkSendRequest request) {
        getDingTalkClient().send(request);
    }

    public static void sendMessage(String message) {
        DingTalkSendRequest request = new DingTalkSendRequest();
        request.setTextContent(message);
        getDingTalkClient().send(request);
    }

    public static void sendException(String message, Throwable e) {
        DingTalkSendRequest request = new DingTalkSendRequest();
        request.setMarkdownTitle("自定义异常");
        request.addMarkdownContent("异常内容", message);
        request.addMarkdownContent("异常信息", ExceptionUtils.exceptionStackTraceText(e));
        getDingTalkClient().send(request);
    }

}
