package approval.dingtalk.controller;

import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkApprovalRecordVo;
import com.wyyt.scp.biz.approval.dingtalk.service.DdApprovalBusinessRelService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wyyt.tool.rpc.Result;

import javax.annotation.Resource;
import java.util.List;

/**
 * 钉钉审批日志 接口层
 *
 * @author wangfarui
 * @since 2023/9/12
 */
@Api(tags = "钉钉审批日志")
@RestController
@RequestMapping("/scp-biz/dingtalk/approvalLog")
public class DingTalkApprovalLogController {

    @Resource
    private DdApprovalBusinessRelService ddApprovalBusinessRelService;

    @GetMapping("/list")
    public Result<List<DingTalkApprovalRecordVo>> queryLogList(@RequestParam("businessId") Long businessId) {
        return Result.ok(ddApprovalBusinessRelService.queryLogList(businessId));
    }

    @GetMapping("/refresh")
    public Result<List<DingTalkApprovalRecordVo>> refreshLog(@RequestParam("businessId") Long businessId) {
        return Result.ok(ddApprovalBusinessRelService.refreshLog(businessId));
    }
}
