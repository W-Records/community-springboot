package com.example.gateway.filter;

import com.example.gateway.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Order(-1) // 定义过滤器顺序
@Component
public class JWTMyGlobalFilter implements GlobalFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Override // exchange获取请求中的数据，chain控制放行
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

//        特殊请求不拦截，直接放行，比如登录接口的请求
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("请求过来的url："+path);
        System.out.println(path.equals("/user/login"));
        if (path.equals("/user/login") || path.equals("/user/register") || path.equals("/article/getArticleList") || path.equals("/article/getArticleCount") || path.equals("/article/getArticleDetail") || path.equals("/comment/getCommentTreeStructuredData") || path.equals("/user/judgeLogInState") || path.equals("/article/getAutocompleteSuggestions") || path.equals("/comment/WebSocketSendMsg")  ) {
            return chain.filter(exchange);
        }

        // 获取请求头中的JWT
        String jwt = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.out.println("请求携带的JWT为："+jwt);

//        如果JWT为空，不放行
        if (jwt == null || jwt == "") {
            // 不放行
            // 校验不通过，不放行
            // 给浏览器返回错误信息
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

            // 返回自定义错误信息
            exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            String errorMessage = "{\"errorMessage\":\"传递的JWT有误\"}";
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));

//            和下面一个return一样，也是终止请求继续，只不过携带了错误信息
            return exchange.getResponse().writeWith(Mono.just(buffer));
            // 返回不放行
            // 返回 Mono<Void> 表示响应已处理完成
//            return exchange.getResponse().setComplete();
        }

        System.out.println("我是if下面的：if (jwt == null || jwt == '')");

        // 校验JWT，验证成功再放行
        try {
            // JWT验证失败 会抛出异常
            Claims claims = jwtUtil.parseJwt(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            // 不放行
            // 校验不通过，不放行
            // 给浏览器返回错误信息
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

            // 返回自定义错误信息
            exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            String errorMessage = "{\"errorMessage\":\"传递的JWT有误\"}";
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));

//            终止请求继续，只不过携带了错误信息
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
//        程序到这里说明用户请求携带的JWT没有问题，可以放行，让其访问我们的微服务
        System.out.println("最后的放行，让其访问我们的微服务");
        return chain.filter(exchange);

    }
}
