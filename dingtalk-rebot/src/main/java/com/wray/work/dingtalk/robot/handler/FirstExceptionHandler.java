package com.wray.work.dingtalk.robot.handler;

import com.alibaba.fastjson.JSONException;
import com.wray.work.common.BizException;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class FirstExceptionHandler extends GenericExceptionHandler implements PriorityOrdered {

    /**
     * 业务异常
     *
     * @param e ErpBizException
     * @return 响应JSON对象
     */
    @ExceptionHandler({BizException.class})
    public Object handleBizException(BizException e, HttpServletRequest request) {
        return super.returnAndHandleException(e, request, null);
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ValidationException.class})
    public Object handleArgumentValidException(Exception e, HttpServletRequest request) {
        BindingResult bindingResult = null;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        }

        String errMessage = "校验错误";
        if (bindingResult != null) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            errMessage = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
        }

        return super.returnAndHandleException(e, request, errMessage);
    }

    /**
     * 处理http请求参数转换异常
     */
    @ExceptionHandler({HttpMessageConversionException.class})
    public Object handleArgumentConversionException(Exception e, HttpServletRequest request) {
        return super.returnAndHandleException(e, request, "参数错误");
    }

    /**
     * 处理 fastjson 异常
     */
    @ExceptionHandler(JSONException.class)
    public Object handleJsonException(JSONException e, HttpServletRequest request) {
        return super.returnAndHandleException(e, request, "数据结构错误");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
