package com.example.commentservice;

import com.example.commentservice.dao.CommentDao;
import com.example.commentservice.pojo.Comment;
import com.example.commentservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class CommentServiceApplicationTests {
    @Autowired
    CommentDao commentDao;
    @Autowired
    CommentService commentService;

    @Test
    void contextLoads() {
//        System.out.println(commentDao.selectCommentTree(1782324867438305282L));
        commentDao.selectCommentTree(1876666669999999L).forEach(commentTree -> {
            System.out.println(commentTree);
        });
    }


//    构建层级评论结构
    @Test
    void test01() {
        commentService.getCommentTree(1876666669999999L);
    }



    @Test
    void test02() {
//        System.out.println(commentDao.insert( new Comment(null, "测试字段添加是否成功", 1782324867438305284L, 1783496495782289411L, null, LocalDateTime.now(), 44)) );
        System.out.println(commentDao.selectById(11111111L));
    }

}
