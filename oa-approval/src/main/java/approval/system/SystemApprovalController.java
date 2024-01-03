package approval.system;

import com.alibaba.fastjson.JSON;
import com.wyyt.platform.authority.annotation.IgnoreCheck;
import com.wyyt.scp.biz.approval.system.callback.CallbackApprovalFlowRequest;
import com.wyyt.scp.biz.approval.system.callback.EncryptionDataRequest;
import com.wyyt.scp.biz.core.annotation.SkipAuthorization;
import com.wyyt.scp.biz.core.utils.EncryptAndDecryptUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wyyt.tool.rpc.Result;

import javax.annotation.Resource;

/**
 * 系统审批接口
 *
 * @author wangfarui
 * @menu 系统审批接口
 * @since 2023/12/12
 */
@Api(tags = "系统审批接口")
@RestController
@RequestMapping("/scp-biz/approval/system")
@Slf4j
public class SystemApprovalController {

    @Resource
    private SystemApprovalService systemApprovalService;

    @Value("${wyys.pwd}")
    private String wyysPwd;

    /**
     * 回调审批流程
     *
     * @param req 审批流程实例回调请求对象
     */
    @PostMapping("/callbackApprovalFlow")
    @SkipAuthorization(directAccess = true)
    @IgnoreCheck
    public Result<Void> callbackApprovalFlow(@RequestBody EncryptionDataRequest req) {
        String decryptData;
        try {
            decryptData = EncryptAndDecryptUtils.getDecryptData(req.getData(), this.wyysPwd);
            log.info("回调审批流程请求对象: {}", decryptData);
        } catch (Exception e) {
            String errMsg = "回调审批流程解密数据错误, 错误数据: " + req.getData();
            log.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
        systemApprovalService.callbackApprovalFlow(JSON.parseObject(decryptData, CallbackApprovalFlowRequest.class));

        return Result.ok();
    }
}
