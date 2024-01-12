package com.wray.work.mybatisplus.fill;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户 服务实现层
 *
 * @author wangfarui
 * @since 2024/1/11
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> {

    @Transactional
    public User save(String name, Integer age, String email) {
        // 初始化User对象
        User user = new User();
        user.setName(name);
        user.setAge(age);
        user.setEmail(email);

        // 保存
        this.save(user);

        // 查询
        return this.getById(user.getId());
    }

    @Transactional
    public User updateByLambda(Long id, Integer age) {
        this.lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getAge, age)
                .update();

        return this.getById(id);
    }

    @Transactional
    public User updateByMethod(Long id, Integer age) {
        User user = new User();
        user.setId(id);
        user.setAge(age);
        this.updateById(user);

        return this.getById(id);
    }

}
