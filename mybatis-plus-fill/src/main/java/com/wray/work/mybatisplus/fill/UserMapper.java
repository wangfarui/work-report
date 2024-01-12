package com.wray.work.mybatisplus.fill;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper管理层
 *
 * @author wangfarui
 * @since 2024/1/11
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
