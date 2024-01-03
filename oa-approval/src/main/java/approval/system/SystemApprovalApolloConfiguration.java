package approval.system;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 钉钉审批的Apollo配置
 *
 * @author wangfarui
 * @since 2023/8/17
 */
@Slf4j
public class SystemApprovalApolloConfiguration implements EnvironmentAware {

    /**
     * 系统OA apollo配置key
     */
    public static final String SYSTEM_OA_APOLLO_KEY = "system.oa.config";

    private Environment environment;

    @PostConstruct
    public void init() {
        String property = this.environment.getProperty(SYSTEM_OA_APOLLO_KEY);
        SystemApprovalConfig.setTemplateCodeMap(this.convertProperty(property));
    }

    @ApolloConfigChangeListener(interestedKeys = SYSTEM_OA_APOLLO_KEY)
    public void apolloListener(ConfigChangeEvent changeEvent) {
        ConfigChange configChange = changeEvent.getChange(SYSTEM_OA_APOLLO_KEY);
        SystemApprovalConfig.setTemplateCodeMap(this.convertProperty(configChange.getNewValue()));
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> convertProperty(String property) {
        return JSON.parseObject(property, Map.class);
    }
}
