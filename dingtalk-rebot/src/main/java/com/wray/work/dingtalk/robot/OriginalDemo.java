package com.wray.work.dingtalk.robot;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 原始HTTP请求的示例
 *
 * @author wangfarui
 * @since 2024/1/12
 */
public class OriginalDemo {

    public static void main(String[] args) {
        // 定义消息内容
        Map<String, String> msg = new HashMap<>();
        msg.put("msgtype", "text");
        msg.put("text", JSONUtil.toJsonStr(new Text("我就是我, 是不一样的烟火")));

        // 发送消息
        HttpResponse httpResponse = HttpUtil.createPost("https://oapi.dingtalk.com/robot/send?access_token=566cc69da782ec******")
                .body(JSONUtil.toJsonStr(msg))
                .execute();

        System.out.println(httpResponse);
    }

    @Data
    @AllArgsConstructor
    static class Text {
        private String content;
    }
}
