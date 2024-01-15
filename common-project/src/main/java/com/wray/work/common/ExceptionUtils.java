package com.wray.work.common;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 *
 * @author wangfarui
 * @since 2022/10/18
 */
public abstract class ExceptionUtils {

    /**
     * 获取异常地调用栈信息
     *
     * @param exception 异常
     * @return 异常地调用栈信息
     */
    public static String exceptionStackTraceText(Throwable exception) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            exception.printStackTrace(pw);
        } catch (Exception e) {
            //ignore
        }
        pw.flush();

        return sw.toString();
    }

    /**
     * 获取异常地调用栈信息
     *
     * @param exception 异常
     * @return 异常地调用栈信息
     */
    public static String exceptionStackTraceText(Throwable exception, int endIndex) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            exception.printStackTrace(pw);
        } catch (Exception e) {
            //ignore
        }
        pw.flush();

        String toString = sw.toString();
        if (toString.length() > endIndex) {
            return toString.substring(0, endIndex);
        }
        return toString;
    }
}
