package approval.system.callback;

import com.wyyt.scp.biz.approval.BusinessApprovalCallbackEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统审批回调事件对象
 *
 * @author wangfarui
 * @since 2023/12/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemApprovalCallbackEvent extends BusinessApprovalCallbackEvent {

}
