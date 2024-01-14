package com.wray.work.dingtalk.robot;

import lombok.Getter;
import lombok.Setter;

/**
 * 钉钉发送消息响应对象
 *
 * @author wangfarui
 * @since 2022/10/18
 */
@Setter
@Getter
public class DingTalkSendResponse {

    private long errcode;

    private String errmsg;
}
