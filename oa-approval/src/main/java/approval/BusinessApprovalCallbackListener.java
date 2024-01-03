package approval;

/**
 * 业务审批回调监听器
 *
 * @author wangfarui
 * @since 2023/12/13
 */
@FunctionalInterface
public interface BusinessApprovalCallbackListener {

    void callback(BusinessApprovalCallbackEvent callbackEvent);
}
