package com.wray.work.mybatisplus.fill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动类
 *
 * @author wangfarui
 * @since 2024/1/11
 */
@SpringBootApplication
@MapperScan
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
