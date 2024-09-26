package com.example.articleservice.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
//此类为Spring的配置类，程序开始运行时就会执行
@Configuration
public class RedisConfig implements WebMvcConfigurer {


//    创建RedisTemplate对象，并放入IOC容器中，通过此对象操作Redis
    @Bean                                                                    // 这个报错不影响程序运行
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("开始创建redis对象....");

        // 创建RedisTemplate对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 下面的代码是 调用 RedisTemplate对象的方法，进行一些设置
        template.setConnectionFactory(connectionFactory); // 选择连接工厂
        // 对键和值进行序列化，序列化是指 将对象 转换为 字节数组，方便存储到Redis中
        //设置序列化器的目的是，在图形化界面能够更直观的看到数据内容，因为如果不设置的话 图形化界面的数据是乱码的（但其实只是显示的是乱码，数据还是正确的）
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }


}
