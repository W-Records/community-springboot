package com.example.articleservice.clients;


import com.example.articleservice.pojo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

// 定义Feign请求的规则
@FeignClient("CommentService") // 指定服务名
public interface CommentClient {

    @GetMapping("/comment/deleteComment") // 指定请求路径
    Result deleteCommentById(@RequestParam("articleId") Long articleId); // 指定请求参数

}
