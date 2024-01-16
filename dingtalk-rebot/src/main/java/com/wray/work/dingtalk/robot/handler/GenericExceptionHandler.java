package com.wray.work.dingtalk.robot.handler;

import cn.hutool.core.date.DateUtil;
import com.wray.work.common.BizException;
import com.wray.work.common.ExceptionUtils;
import com.wray.work.common.Result;
import com.wray.work.common.UserUtils;
import com.wray.work.dingtalk.robot.DingTalkHelper;
import com.wray.work.dingtalk.robot.DingTalkProperties;
import com.wray.work.dingtalk.robot.DingTalkSendRequest;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;

/**
 * 通用异常处理器
 *
 * @author wangfarui
 * @since 2022/10/19
 */
@Slf4j
public abstract class GenericExceptionHandler implements InitializingBean {

    @Autowired
    private DingTalkProperties dingTalkProperties;

    @Value("${spring.profiles.active:}")
    private String environment;

    /**
     * 是否为生产环境
     */
    private boolean isProductionEnv;

    /**
     * 服务环境
     */
    private String envStr;

    @Override
    public void afterPropertiesSet() {
        String env = this.environment;
        if (StringUtils.isNotBlank(env)) {
            if (env.contains("dev")) {
                envStr = "开发环境";
            } else if (env.contains("test")) {
                envStr = "测试环境";
            } else if (env.contains("pro")) {
                isProductionEnv = true;
                envStr = "生产环境";
            } else {
                envStr = "未知环境";
            }
        }
    }

    protected Object returnAndHandleException(Throwable e, HttpServletRequest request, String errMessage) {
        boolean isSendDingTalk = this.dingTalkProperties.isOpenAllWarning();
        if (isSendDingTalk) {
            return returnAndHandleException(e, request, errMessage, true);
        }
        if (e instanceof BizException) {
            isSendDingTalk = true;
        }
        if (request != null) {
            if (isSendDingTalk) {
                isSendDingTalk = !this.dingTalkProperties.getIgnoreUrls().contains(request.getRequestURI());
            } else {
                isSendDingTalk = this.dingTalkProperties.getWarnUrls().contains(request.getRequestURI());
            }
        }
        return returnAndHandleException(e, request, errMessage, isSendDingTalk);
    }

    protected Object returnAndHandleException(Throwable e, HttpServletRequest request,
                                              String errMessage, boolean isSendDingTalk) {
        final String errMsg = ExceptionUtils.exceptionStackTraceText(e);
        log.error(errMsg, e);
        if (isSendDingTalk) {
            sendDingTalkMessage(e, request);
        }
        return Result.error(this.isProductionEnv && StringUtils.isNotBlank(errMessage) ? errMessage : errMsg);
    }

    private void sendDingTalkMessage(Throwable e, HttpServletRequest httpServletRequest) {
        // 初始化钉钉告警对象 并配置告警标题
        DingTalkSendRequest request = new DingTalkSendRequest();
        request.setMarkdownTitle(e instanceof BizException ? "业务告警" : "系统告警");

        // 配置基础告警信息
        request.addMarkdownContent("【告警环境】", this.envStr);
        request.addMarkdownContent("【traceId】", MDC.get("traceId"));
        request.addMarkdownContent("【租户id】", UserUtils.getTenantId().toString());
        request.addMarkdownContent("【告警时间】", DateUtil.now());
        // 配置http请求告警信息
        if (httpServletRequest != null) {
            request.addMarkdownContent("【异常接口】", httpServletRequest.getRequestURI());
        }
        // 异常堆栈信息
        request.addMarkdownContent("【异常堆栈】", ExceptionUtils.exceptionStackTraceText(e, 1000));

        DingTalkHelper.send(request);
    }
}
