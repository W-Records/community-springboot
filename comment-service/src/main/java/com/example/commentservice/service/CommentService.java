package com.example.commentservice.service;

import com.example.commentservice.pojo.VO.CommentTree;

import java.util.List;

//为了更好的扩展，实现多态，定义此接口，后续接收实现类对象
public interface CommentService {
    public List<CommentTree> getCommentTree(Long articleId);
}
