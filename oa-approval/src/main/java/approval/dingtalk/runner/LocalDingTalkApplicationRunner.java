package approval.dingtalk.runner;

import com.wyyt.scp.biz.approval.dingtalk.service.DdApprovalFormRelService;
import lombok.extern.slf4j.Slf4j;

/**
 * 本地钉钉应用 Runner
 *
 * @author wangfarui
 * @since 2023/8/17
 */
@Slf4j
public class LocalDingTalkApplicationRunner extends AbstractDingTalkApplicationRunner {

    public LocalDingTalkApplicationRunner(DdApprovalFormRelService ddApprovalFormRelService) {
        super(ddApprovalFormRelService);
    }
}
