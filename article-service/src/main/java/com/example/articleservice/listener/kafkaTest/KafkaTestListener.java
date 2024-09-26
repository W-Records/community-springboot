package com.example.articleservice.listener.kafkaTest;

import com.alibaba.fastjson.JSON;
import com.example.articleservice.pojo.ArticleCategory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//    Kafka测试
@Component
public class KafkaTestListener {

//    Kafka：此方法监听sportsNews主题的消息
    @KafkaListener(topics = "sportsNews")
    public void ListenerGetMessage(String message) {
        ArticleCategory articleCategory = JSON.parseObject(message, ArticleCategory.class);
        System.out.println(getClass()+"监听主题获取到的消息: " + message);
        System.out.println(getClass()+"监听主题获取到的消息(转成对象): " + articleCategory);
    }

    //    测试KafkaStream
    @KafkaListener(topics = "itcast-topic-out")
    public void ListenerGetMessage2(String message) {
        System.out.println("测试KafkaStream_监听itcast-topic-out主题获取到的消息: " + message);
    }

}
