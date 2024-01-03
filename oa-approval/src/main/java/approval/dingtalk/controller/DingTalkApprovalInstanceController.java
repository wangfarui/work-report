package approval.dingtalk.controller;

import com.wyyt.scp.biz.approval.dingtalk.service.DdApprovalBusinessRelService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wyyt.tool.rpc.Result;

import javax.annotation.Resource;

/**
 * 钉钉审批实例 接口层
 *
 * @author wangfarui
 * @menu 钉钉审批-审批实例
 * @since 2023/11/9
 */
@Api(tags = "钉钉审批实例")
@RestController
@RequestMapping("/scp-biz/dingtalk/approvalInstance")
public class DingTalkApprovalInstanceController {

    @Resource
    private DdApprovalBusinessRelService ddApprovalBusinessRelService;

    /**
     * 撤销审批
     *
     * @param businessId 业务表主键id
     */
    @GetMapping("/terminate")
    public Result<Void> terminate(@RequestParam("businessId") Long businessId) {
        ddApprovalBusinessRelService.terminateProcessInstance(businessId);
        return Result.ok();
    }
}
