package approval.dingtalk.config;

import com.wyyt.scp.biz.approval.dingtalk.runner.AbstractDingTalkApplicationRunner;
import com.wyyt.scp.biz.approval.dingtalk.runner.DefaultDingTalkApplicationRunner;
import com.wyyt.scp.biz.approval.dingtalk.runner.LocalDingTalkApplicationRunner;
import com.wyyt.scp.biz.approval.dingtalk.service.DdApprovalFormRelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 钉钉审批自动装配
 *
 * @author wangfarui
 * @since 2023/8/16
 */
@Configuration(proxyBeanMethods = false)
@Import({DingTalkApprovalApolloConfiguration.class, DingTalkApprovalCallbackProcessor.class})
@Slf4j
public class DingTalkApprovalAutoConfiguration {

    @Value("${os.name}")
    private String osName;

    @Value("${dingtalk.oa.runner.enabled:false}")
    private boolean enabled;

    @Bean
    @ConditionalOnMissingBean
    public AbstractDingTalkApplicationRunner defaultDingTalkRunner(DdApprovalFormRelService ddApprovalFormRelService) {
        if (this.enabled || (StringUtils.isBlank(this.osName) || this.osName.contains("Linux"))) {
            log.info("钉钉配置：启用默认钉钉应用启动器");
            return new DefaultDingTalkApplicationRunner(ddApprovalFormRelService);
        } else {
            log.info("钉钉配置: 启用本地钉钉应用启动器, osName: " + this.osName);
            return new LocalDingTalkApplicationRunner(ddApprovalFormRelService);
        }
    }
}
