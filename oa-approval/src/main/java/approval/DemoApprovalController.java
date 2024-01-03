package approval;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 示例审批接口<p>
 * 演示钉钉审批和系统审批的使用示例
 *
 * @author wangfarui
 * @since 2023/12/13
 */
@RestController
@RequestMapping("/scp-biz/approval/demo")
public class DemoApprovalController {

    @Resource
    private DemoApprovalService demoApprovalService;

    @GetMapping("/startSystemApproval")
    public void startSystemApproval(@RequestParam("name") String name) {
        demoApprovalService.startSystemApproval(name);
    }

    @GetMapping("/startDingTalkApproval")
    public void startDingTalkApproval(@RequestParam("businessId") Long businessId) {
        demoApprovalService.startDingTalkApproval(businessId);
    }
}
