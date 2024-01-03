package approval.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统审批-流程表单控件
 *
 * @author wangfarui
 * @since 2022/11/11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SystemFormComponent {

    /**
     * 表单控件名称
     * <p>对应物易云商租户表单模板控件的标题</p>
     */
    String value();

    /**
     * 表单控件的次级控件名称
     * <p>非特殊组件，一般不填</p>
     */
    String subName() default "";

    /**
     * 是否为必填项
     * <p>不确定情况下，默认必填</p>
     */
    boolean required() default true;

    /**
     * 字段为{@link java.util.Date}时，日期格式化样式
     */
    String pattern() default "yyyy-MM-dd HH:mm:ss";
}
