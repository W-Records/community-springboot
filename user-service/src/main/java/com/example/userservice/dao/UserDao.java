package com.example.userservice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.userservice.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<User> {

    @Select("select * from user where user_name = #{userName}")
    public User selectByUserName(String userName);

}
