package approval.dingtalk.config;

import com.wyyt.scp.biz.approval.ApprovalCallbackProcessor;
import com.wyyt.scp.biz.approval.dingtalk.annotation.DingTalkApprovalCallback;
import com.wyyt.scp.biz.approval.dingtalk.listener.DingTalkCallbackListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 钉钉审批回调的后置处理器
 *
 * @author wangfarui
 * @see DingTalkApprovalCallback
 * @since 2023/8/16
 */
public class DingTalkApprovalCallbackProcessor extends ApprovalCallbackProcessor {

    protected void processMethod(Object bean, Method method) {
        DingTalkApprovalCallback annotation = AnnotationUtils.findAnnotation(method, DingTalkApprovalCallback.class);
        if (annotation == null) {
            return;
        }

        super.checkArgument(method);

        DingTalkCallbackListener dingTalkCallbackListener = (callbackEvent) ->
                ReflectionUtils.invokeMethod(method, bean, callbackEvent);

        DingTalkApprovalConfig.addCallbackListener(annotation, dingTalkCallbackListener);
    }
}
