package com.example.userservice.controller;

import com.example.userservice.dao.UserDao;
import com.example.userservice.pojo.Result;
import com.example.userservice.pojo.User;
import com.example.userservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserDao userDao;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    HttpServletRequest httpServletRequest;


//    登录生成JWT
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        
        log.info("请求到达这里_登录请求");
        System.out.println("user: " + user);

//        判断用户密码是否正确
        User returnUser = userDao.selectByUserName(user.getUserName());
        System.out.println("returnUser: " + returnUser);
        if( returnUser == null || !returnUser.getUserPassword().equals(user.getUserPassword()) ){
            return Result.error("用户名 或 密码 错误");
        }
//        程序到这里 说明用户名 密码 正确，生成JWT并返回给前端
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", returnUser.getUserId());
        claims.put("userName", returnUser.getUserName());
        String jwt = jwtUtil.getJwt(claims);
        System.out.println("生成的JWT为：" + jwt);

        Claims claims1 = jwtUtil.parseJwt(jwt);
        System.out.println( "JWT解析后取出来的值：" + claims1 );

        return Result.succeed(jwt);
    }
//    注册
    @PostMapping("/register")
    public Result register(@RequestBody User user) {

        log.info("请求到达这里_注册请求");
        System.out.println(user);

        try {
            userDao.insert(user);
        }
        catch (Exception e) {
            e.printStackTrace();
            return Result.error("用户已存在");
        }
        return Result.succeed();
    }


    //    测试是否登录过拿到token
    @RequestMapping("/TestWhetherGetToken")
    public String TestWhetherGetToken() {
        log.info("请求到达这里_测试请求：说明拿到Token");
        System.out.println("请求头中取出来的JWT：" + httpServletRequest.getHeader("Authorization"));
        System.out.println("请求头中取出来的JWT_携带的用户消息：" + jwtUtil.parseJwt(httpServletRequest.getHeader("Authorization"))  );
        return "后端接口测试内容成功返回！！！！！";
    }


//    测试是否是登录状态
    @GetMapping("/judgeLogInState")
    public Result judgeLogInState() {
        //获取当前请求的用户信息
        Claims userClaims = null;
        try {
            userClaims = jwtUtil.parseJwt(httpServletRequest.getHeader("Authorization"));
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("出现异常走到catch中：未登录啊");
            return Result.error("未登录啊");
        }
//        程序到这里说明已经登录
        System.out.println("userClaims: " + userClaims);
        Long userId = userClaims.get("userId", Long.class);
        String userName = userClaims.get("userName", String.class);
        System.out.println("userName: " + userName);
        System.out.println("userId: " + userId);
        User user = new User(userId,userName,null,null);

        return Result.succeed(user);
    }
}
