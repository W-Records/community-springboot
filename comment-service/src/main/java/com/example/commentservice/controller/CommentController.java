package com.example.commentservice.controller;

import com.alibaba.fastjson.JSON;
import com.example.commentservice.dao.CommentDao;
import com.example.commentservice.pojo.Comment;
import com.example.commentservice.pojo.Result;
import com.example.commentservice.pojo.VO.CommentTree;
import com.example.commentservice.pojo.VO.ReplyComment;
import com.example.commentservice.service.CommentService;
import com.example.commentservice.utils.JwtUtil;
import com.example.commentservice.webSocket.WebSocketServer;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    CommentDao commentDao;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private WebSocketServer webSocketServer;

//    获取层级评论结构
    @GetMapping("/getCommentTreeStructuredData")
    public Result getCommentTreeStructuredData(Long articleId) {
        List<CommentTree> commentTree = commentService.getCommentTree(articleId);
        return Result.succeed(commentTree);
    }

//    发布评论
    @PostMapping("/publishComment")
    public Result publishComment(@RequestBody Comment comment) {
        System.out.println("前端传递过来的评论数据："+comment);

        //获取当前请求的用户信息
        Claims userClaims = jwtUtil.parseJwt(httpServletRequest.getHeader("Authorization"));
        Long userId = userClaims.get("userId", Long.class);
        comment.setUserId(userId);
        comment.setCreateTime(LocalDateTime.now());
        System.out.println("完善信息后的评论数据："+comment);
//      返回值insert表示插入的行数
        Integer insert = commentDao.insert(comment);
        System.out.println("返回值insert表示插入的行数："+insert);

        //        向CommentReplyNotice主题发送消息
        Comment selectParentId = commentDao.selectParentId(comment.getParentId(), userId);
        if (selectParentId != null){
            kafkaTemplate.send("CommentReplyNotice", selectParentId.getUserId().toString());
        }



        return Result.succeed(insert);
    }


//    返回评论回复数据
    @GetMapping("/getReplyComment")
    public Result getReplyComment(Long userId) {
        System.out.println("前端传递过来的userId："+userId);
        List<ReplyComment> replyComments = commentDao.selectReply(userId);
        System.out.println("返回的回复数据："+replyComments);
        replyComments.forEach(replyComment -> {
            System.out.println(replyComment);
        });
//        去除自己回复自己的消息
        replyComments.removeIf(replyComment -> replyComment.getUserId().equals(userId));
        System.out.println("去除自己回复自己的消息："+replyComments);
        replyComments.forEach(replyComment -> {
            System.out.println(replyComment);
        });
        return Result.succeed(replyComments);
    }


//    返回未读评论数
    @GetMapping("/getNotReadCommentCount")
    public Result getNotReadCommentCount(Long userId) {
        System.out.println("前端传递过来的userId："+userId);
        Integer notReadCommentCount = commentDao.selectNotRead(userId);
        System.out.println("返回的未读评论数："+notReadCommentCount);
        return Result.succeed(notReadCommentCount);
    }

//    将未读评论改为已读
    @GetMapping("/changeCommentReadOrNot")
    public Result changeCommentReadOrNot(Long userId) {
        System.out.println("前端传递过来的userId："+userId);
        List<Comment> comments = commentDao.selectReplyNoOtherTable(userId);
        System.out.println("查询出来的回复数据（没有修改）："+comments);
//        将comments的readOrNot属性改为1
        comments.forEach(comment -> {
            comment.setReadOrNot(1);
            commentDao.updateById(comment);
        });
        System.out.println("查询出来的回复数据（修改后）："+comments);
        return Result.succeed();
    }






//    接收文章id，删除此文章下的所有评论
    @GetMapping("/deleteComment")
    public Result deleteComment(Long articleId) {
        System.out.println("feign请求传递过来的articleId："+articleId);
        commentDao.deleteCommentById(articleId);
        return Result.succeed();
    }





    // 与亿豪测试 WebSocket长连接
    @GetMapping("/WebSocketSendMsg")
    public Result WebSocketSendMsg(String userName, String message) {
        webSocketServer.sendOneMessage(userName, message);
        return Result.succeed();
    }

}
