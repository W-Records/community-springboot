package com.example.gateway.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

//jwt工具类，生成jwt和认证
@Component
public class JwtUtil {


    //签名密钥
    @Value("${token.jwt.signing-key}")  // static属性 无法实现注入，且类要IOC容器管理
    private String signingKey;

    //有效时间
    @Value("${token.jwt.expire}")
    private Long expire;


    /**
     * @Description: 生成令牌
     * @Param: Map
     * @Return: String
     */
    public String getJwt(Map<String, Object> claims) {

//        jwt本身就是一个 字符串，
//        它一共由3个部分组成，第二个部分存储我们自己的数据，
//        所以当我们生成 完整的jwt后，需要最后调用compact()转换成字符串
        return Jwts.builder()
                .setClaims(claims) //设置载荷内容,存储我们自己的数据
                .signWith(SignatureAlgorithm.HS256, signingKey) //设置签名算法
                .setExpiration(new Date(System.currentTimeMillis() + expire)) //设置有效时间
                .compact();
    }

    /**
     * @Description: 解析令牌
     * @Param: String
     * @Return: Claims
     */
    public Claims parseJwt(String jwt) {

//        Claims claims接收的是 Jwt中自己存储的数据
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey) //指定签名密钥，与生成密钥相同
                .parseClaimsJws(jwt) //开始解析令牌,令牌错误 会报出异常
                .getBody(); // 获取自定义内容部分

        return claims;
    }

}
