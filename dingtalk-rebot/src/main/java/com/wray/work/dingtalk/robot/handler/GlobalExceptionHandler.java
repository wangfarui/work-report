package com.wray.work.dingtalk.robot.handler;

import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * <p>在 自定义异常 后生效</p>
 * <p>在 org.wyyt.springcloud.advice.ExceptionControllerAdvice 前生效</p>
 *
 * @author wangfarui
 * @since 2022/10/19
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends GenericExceptionHandler implements Ordered {

    @ExceptionHandler(Throwable.class)
    public Object handleOtherException(final Throwable e, HttpServletRequest request) {
        // 走到全局异常处理器的异常，必须要钉钉告警提示
        return returnAndHandleException(e, request, "服务器错误", true);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
