package approval.dingtalk.annotation;

import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.ApprovalEventType;
import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkApprovalCallbackEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 钉钉审批回调
 *
 * <p>回调事件方法没有token！！！不要使用UserUtils等工具类</p>
 *
 * @author wangfarui
 * @see DingTalkApprovalCallbackEvent
 * @since 2023/8/16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DingTalkApprovalCallback {

    /**
     * 业务审批类型
     */
    ApprovalBusinessTypeEnum value();

    /**
     * 审批事件类型
     * <p>默认所有审批事件都回调</p>
     */
    ApprovalEventType eventType();
}
