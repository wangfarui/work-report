package com.wray.work.common;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 响应结果
 *
 * @author wangfarui
 * @since 2024/1/15
 */
public class Result<T> {

    @ApiModelProperty("编码")
    private Long code;

    @ApiModelProperty("当业务出现错误时才有的错误信息")
    private String error;

    @ApiModelProperty("消息")
    private String message;

    @ApiModelProperty("业务处理是否成功. true:成功; false:失败")
    private Boolean ok;

    @ApiModelProperty("当数据是集合时, 返回集合总共的数据(用于分页)")
    private Long count;

    @ApiModelProperty("返回给调用方的具体数据")
    private T data;

    public Result() {
    }

    private Result(final long code,
                   final String error,
                   final String message,
                   final T data,
                   final Long count,
                   final Boolean ok) {
        this.code = code;
        this.error = error;
        this.message = message;
        this.data = data;
        this.count = count;
        this.ok = ok;
    }

    public static <T> Result<T> ok(final long code,
                                   final T data) {
        return new Result<>(code, null, null, data, null, true);
    }

    public static <T> Result<List<T>> ok(final List<T> data,
                                         final long count) {
        return new Result<>(ResultCode.SUCCESS.getCode(), null, null, data, count, ResultCode.SUCCESS.getSuccess());
    }

    public static <T> Result<T> ok(final T data) {
        return ok(ResultCode.SUCCESS.getCode(), data);
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> error(final long code,
                                      final String error,
                                      final T data) {
        return new Result<>(code, error, error, data, null, false);
    }

    public static <T> Result<T> error(final long code,
                                      final Exception exception,
                                      final T data) {
        return error(code, ExceptionUtils.exceptionStackTraceText(exception), data);
    }

    public static <T> Result<T> error(final long code,
                                      final String error) {
        return error(code, error, null);
    }

    public static <T> Result<T> error(final long code,
                                      final Exception exception) {
        return error(code, exception, null);
    }

    public static <T> Result<List<T>> error(final String error,
                                            final List<T> data,
                                            final long count) {
        return new Result<>(ResultCode.ERROR.getCode(), error, error, data, count, ResultCode.ERROR.getSuccess());
    }

    public static <T> Result<T> error(final T data) {
        return error(ResultCode.ERROR.getCode(), "", data);
    }

    public static <T> Result<T> error(final String error) {
        return error(ResultCode.ERROR.getCode(), error, null);
    }

    public static <T> Result<T> error() {
        return error(null);
    }
}
