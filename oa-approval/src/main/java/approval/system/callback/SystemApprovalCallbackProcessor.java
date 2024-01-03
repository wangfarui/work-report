package approval.system.callback;

import com.wyyt.scp.biz.approval.ApprovalCallbackProcessor;
import com.wyyt.scp.biz.approval.system.SystemApprovalConfig;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 系统审批回调的后置处理器
 *
 * @author wangfarui
 * @see SystemApprovalCallback
 * @since 2023/8/16
 */
public class SystemApprovalCallbackProcessor extends ApprovalCallbackProcessor {

    @Override
    protected void processMethod(Object bean, Method method) {
        SystemApprovalCallback annotation = AnnotationUtils.findAnnotation(method, SystemApprovalCallback.class);
        if (annotation == null) {
            return;
        }

        super.checkArgument(method);

        SystemCallbackListener callbackListener = (callbackEvent) ->
                ReflectionUtils.invokeMethod(method, bean, callbackEvent);

        SystemApprovalConfig.addCallbackListener(annotation, callbackListener);
    }


}
