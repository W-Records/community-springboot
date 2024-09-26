package com.example.articleservice.controller.testKafka;

import com.alibaba.fastjson.JSON;
import com.example.articleservice.pojo.ArticleCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


//    Kafka测试
@RestController
public class MyTestKafka {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/testKafkasend")
    public String testKafkasend() {
        ArticleCategory articleCategory = new ArticleCategory(2390320196699L, "蔡徐坤玩体育");
//        向sportsNews主题发送消息
        kafkaTemplate.send("sportsNews", JSON.toJSONString(articleCategory));
        return "接口返回的数据";
    }

//    测试KafkaStream
    @GetMapping("/testKafkaStream")
    public String testKafkaStream() {

    //        向sportsNews主题发送消息
        kafkaTemplate.send("itcast-topic-input", "hello Tom");
        return "接口返回的数据";
    }
}
