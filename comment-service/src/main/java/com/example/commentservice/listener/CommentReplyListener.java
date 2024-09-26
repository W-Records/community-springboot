package com.example.commentservice.listener;

import com.alibaba.fastjson.JSON;
import com.example.commentservice.webSocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CommentReplyListener {
    @Autowired
    private WebSocketServer webSocketServer;

    //    Kafka：此方法监听CommentReplyNotice主题的消息
    @KafkaListener(topics = "CommentReplyNotice")
    public void ListenerGetMessage(String message) {

        System.out.println("监听CommentReplyNotice主题获取到的消息: " + message);

        webSocketServer.sendOneMessage(message,"有人回复评论了");

    }

}
