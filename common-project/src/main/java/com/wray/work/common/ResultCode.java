package com.wray.work.common;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum ResultCode {
    SUCCESS(200L, true, "SUCCESS"),
    ERROR(500L, false, "ERROR"),
    UN_AUTHORIZED(403L, false, "未授权(未携带token访问接口)"),
    CUSTOMIZE_ERROR(417L, false, "后台自定义服务异常(后台的业务报错都是这个，直接展示message)"),
    ILLEGAL_ACCESS(100011L, false, "非法访问(携带错误的token访问系统)"),
    UNABLE_TO_ACCESS(100012L, false, "服务暂时无法访问(微服务无法访问，某个服务宕机)"),
    DATABASE_EXCEPTION(100013L, false, "数据库访问异常(数据库异常)");

    private final Long code;
    private final String description;
    private final Boolean success;

    ResultCode(final long code,
               final Boolean success,
               final String description) {
        this.code = code;
        this.success = success;
        this.description = description;
    }

    public static ResultCode get(final Long code) {
        if (null == code) {
            return null;
        }

        for (final ResultCode item : ResultCode.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static ResultCode get(final String description) {
        if (!StringUtils.hasText(description)) {
            return null;
        }

        for (final ResultCode item : ResultCode.values()) {
            if (item.getDescription().equals(description)) {
                return item;
            }
        }
        return null;
    }
}