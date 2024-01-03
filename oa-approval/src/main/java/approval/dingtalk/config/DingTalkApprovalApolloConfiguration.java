package approval.dingtalk.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.wyyt.scp.biz.approval.dingtalk.runner.AbstractDingTalkApplicationRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉审批的Apollo配置
 *
 * @author wangfarui
 * @since 2023/8/17
 */
@Slf4j
public class DingTalkApprovalApolloConfiguration implements EnvironmentAware, ApplicationContextAware {

    /**
     * 钉钉OA apollo配置key
     */
    public static final String DING_TALK_OA_APOLLO_KEY = "dingtalk.oa.config";

    private Environment environment;

    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        String property = this.environment.getProperty(DING_TALK_OA_APOLLO_KEY);
        DingTalkApprovalConfig.putTenantProperties(this.convertProperty(property));
    }

    @ApolloConfigChangeListener(interestedKeys = DING_TALK_OA_APOLLO_KEY)
    public void apolloListener(ConfigChangeEvent changeEvent) {
        ConfigChange configChange = changeEvent.getChange(DING_TALK_OA_APOLLO_KEY);
        DingTalkApprovalConfig.cleanTimerCache();
        DingTalkApprovalConfig.putTenantProperties(this.convertProperty(configChange.getNewValue()));
        try {
            applicationContext.getBean(AbstractDingTalkApplicationRunner.class).refresh();
        } catch (BeansException e) {
            log.error("[DingTalkApplicationRunner]Bean未找到", e);
        }
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, DingTalkTenantProperties> convertProperty(String property) {
        Map<String, JSONObject> map = JSON.parseObject(property, Map.class);
        Map<Long, DingTalkTenantProperties> result = new HashMap<>();
        for (Map.Entry<String, JSONObject> entry : map.entrySet()) {
            JSONObject object = entry.getValue();
            DingTalkTenantProperties properties = new DingTalkTenantProperties();
            properties.setTenantName(object.getString("tenantName"));
            properties.setAgentId(object.getLong("agentId"));
            properties.setAppKey(object.getString("appKey"));
            properties.setAppSecret(object.getString("appSecret"));
            properties.setUserId(object.getString("userId"));
            result.put(Long.valueOf(entry.getKey()), properties);
        }
        return result;
    }
}
