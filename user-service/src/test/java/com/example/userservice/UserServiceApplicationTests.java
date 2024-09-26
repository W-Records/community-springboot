package com.example.userservice;

import com.example.userservice.dao.UserDao;
import com.example.userservice.pojo.User;
import com.example.userservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class UserServiceApplicationTests {

    @Autowired
    UserDao userDao;
    @Autowired
    JwtUtil jwtUtil;
    @Test
    void contextLoads() {

//        userDao.insert(new User(null,"Brom","123456",""));
//
//        System.out.println( userDao.selectById(1) );
        System.out.println( userDao.selectList(null) );



    }


//测试JWT
    @Test
    void test01() {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", "3201");
        claims.put("userName", "tom哈哈哈哈哈没问题");
        String jwt = jwtUtil.getJwt(claims);
        System.out.println("生成的JWT为：" + jwt);
    }
    @Test
    void test02() {
        Claims claims = jwtUtil.parseJwt("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmFtZSI6InRvbeWTiOWTiOWTiOWTiOWTiOayoemXrumimCIsImV4cCI6MTcxMzcwMzc5MSwidXNlcklkIjoiMzIwMSJ9.EMoXYrB0wMhTNo3unrIrwvdssE6bB9j5DLSa1cJA-IM");
        System.out.println(claims);
        String userId = claims.get("userId", String.class);
        System.out.println(userId);
    }

}
