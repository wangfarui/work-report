package approval;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

/**
 * 审批应用Runner
 *
 * @author wangfarui
 * @since 2023/12/13
 */
public abstract class ApprovalApplicationRunner implements ApplicationRunner, Ordered {

    @Override
    public void run(ApplicationArguments args) {
        this.innerRun();
        this.outerRun();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * 外部启动器扩展运行项
     */
    protected void outerRun() {

    }

    private void innerRun() {
        ApprovalBusinessTypeEnum.verifyEnumSelf();
    }

}
