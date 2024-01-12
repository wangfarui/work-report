package com.wray.work.mybatisplus.fill.test;

import com.wray.work.mybatisplus.fill.User;
import com.wray.work.mybatisplus.fill.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用户服务 单元测试类
 *
 * @author wangfarui
 * @since 2024/1/12
 */
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void testSaveUser() {
        User user = userService.save("wray", 18, "wray20156294@gmail.com");
        System.out.println(user);
    }

    @Test
    public void testUpdateByLambda() {
        User user = userService.updateByLambda(1745704644463579138L, 19);
        System.out.println(user);
    }

    @Test
    public void testUpdateByMethod() {
        User user = userService.updateByMethod(1745704644463579138L, 20);
        System.out.println(user);
    }
}
