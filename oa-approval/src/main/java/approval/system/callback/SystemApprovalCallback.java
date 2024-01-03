package approval.system.callback;

import com.wyyt.scp.biz.approval.ApprovalBusinessTypeEnum;
import com.wyyt.scp.biz.approval.ApprovalEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统审批回调
 *
 * @author wangfarui
 * @since 2023/12/12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SystemApprovalCallback {

    /**
     * 业务审批类型
     */
    ApprovalBusinessTypeEnum value();

    /**
     * 审批事件类型
     * <p>系统审批不支持 ApprovalEventType.ALL </p>
     */
    ApprovalEventType eventType();
}
