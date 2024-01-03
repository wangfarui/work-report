package approval.dingtalk.annotation;

import com.wyyt.scp.biz.approval.dingtalk.model.ComponentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 钉钉表单控件
 *
 * @author wangfarui
 * @since 2022/11/11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DingTalkFormComponent {

    /**
     * 表单控件名称
     */
    String value();

    /**
     * 表单控件id
     * <p>表单控件列表中唯一</p>
     */
    String id() default "";

    /**
     * 表单控件类型
     */
    ComponentType componentType() default ComponentType.AUTO;

    /**
     * 是否非空, 默认不能为空
     */
    boolean required() default false;

    /**
     * 字段为{@link java.util.Date}时，日期格式化样式
     */
    String pattern() default "yyyy-MM-dd HH:mm:ss";
}
