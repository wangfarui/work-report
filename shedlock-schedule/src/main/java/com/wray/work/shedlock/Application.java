package com.wray.work.shedlock;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 应用
 *
 * @author wangfarui
 * @since 2024/1/9
 */
@ComponentScan(basePackageClasses = Application.class)
public class Application {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(Application.class);
        applicationContext.refresh();
    }
}
