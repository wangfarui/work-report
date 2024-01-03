package approval.dingtalk.listener;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.dingtalk.open.app.api.GenericEventListener;
import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;
import com.wyyt.big.platform.client.util.LogTranceUtil;
import com.wyyt.scp.biz.approval.dingtalk.model.DingTalkCallbackChange;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * 钉钉Stream默认监听器
 *
 * @author wangfarui
 * @since 2023/8/21
 */
@Slf4j
public abstract class AbstractDingTalkStreamEventListener implements GenericEventListener {

    /**
     * 审批任务回调
     */
    protected static final String BPMS_TASK_CHANGE = "bpms_task_change";

    /**
     * 审批实例回调
     */
    protected static final String BPMS_INSTANCE_CHANGE = "bpms_instance_change";

    @Override
    public EventAckStatus onEvent(GenericOpenDingTalkEvent event) {
        MDC.put(LogTranceUtil.TRACE_KEY, UUID.randomUUID().toString().replace("-", ""));
        log.info("钉钉回调事件对象: " + JSON.toJSONString(event));
        try {
            //事件类型
            String eventType = event.getEventType();

            // 确定事件类型，决定data
            switch (eventType) {
                case BPMS_TASK_CHANGE:
                case BPMS_INSTANCE_CHANGE:
                    DingTalkCallbackChange change = BeanUtil.copyProperties(event, DingTalkCallbackChange.class);
                    handleApprovalEvent(change);
                    break;
                default: {
                    // 未知的事件类型，忽略
                }
            }

            //消费成功
            return EventAckStatus.SUCCESS;
        } catch (Exception e) {
            log.error("钉钉回调事件消费失败", e);
            //消费失败
            return EventAckStatus.LATER;
        } finally {
            MDC.remove(LogTranceUtil.TRACE_KEY);
        }
    }

    protected void handleApprovalEvent(DingTalkCallbackChange eventChange) {

    }
}
