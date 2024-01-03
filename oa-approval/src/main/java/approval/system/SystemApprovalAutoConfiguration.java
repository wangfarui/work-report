package approval.system;

import com.wyyt.scp.biz.approval.system.callback.SystemApprovalCallbackProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 系统审批自动装配
 *
 * @author wangfarui
 * @since 2023/12/13
 */
@Configuration(proxyBeanMethods = false)
@Import({SystemApprovalApolloConfiguration.class, SystemApprovalCallbackProcessor.class})
@Slf4j
public class SystemApprovalAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SystemApprovalApplicationRunner defaultSystemApprovalRunner() {
        return new SystemApprovalApplicationRunner();
    }
}
