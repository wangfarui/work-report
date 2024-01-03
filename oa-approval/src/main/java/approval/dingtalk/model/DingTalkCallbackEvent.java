package approval.dingtalk.model;

import lombok.Data;

import java.util.Date;

/**
 * 钉钉回调事件
 *
 * @author wangfarui
 * @since 2023/8/16
 */
@Data
public abstract class DingTalkCallbackEvent {

    /**
     * 事件id
     */
    private String eventId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件发生时间
     */
    private Date eventBornTime;
}
