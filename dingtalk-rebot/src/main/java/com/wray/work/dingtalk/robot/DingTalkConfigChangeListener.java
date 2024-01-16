package com.wray.work.dingtalk.robot;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * 钉钉配置信息变更监听器
 *
 * @author wangfarui
 * @since 2022/10/18
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
public class DingTalkConfigChangeListener {

    @Autowired
    private DingTalkClient dingTalkClient;

    @ApolloConfigChangeListener(interestedKeyPrefixes = {"dingTalk"})
    private void onChange(ConfigChangeEvent changeEvent) {
        Set<String> changedKeys = changeEvent.changedKeys();
        for (String changedKey : changedKeys) {
            ConfigChange change = changeEvent.getChange(changedKey);
            log.info("钉钉配置参数变更: {}", change.toString());
        }

        DingTalkProperties.changeProperties(changeEvent);

        String key = "dingTalk.enabled";
        if (changeEvent.isChanged(key)) {
            ConfigChange change = changeEvent.getChange(key);
            String newValue = change.getNewValue();
            boolean b = Boolean.parseBoolean(newValue);
            DingTalkClient.changeCanApply(b);
            pushToDingTalk(b);
        }
    }

    private void pushToDingTalk(boolean b) {
        DingTalkSendRequest request = new DingTalkSendRequest();
        request.setDingTalkMsgType(DingTalkMsgType.TEXT);
        request.setTextContent("钉钉告警已" + (b ? "开启" : "关闭"));
        dingTalkClient.send(request, true);
    }

}
