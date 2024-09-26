package com.example.commentservice.service.impl;

import com.example.commentservice.dao.CommentDao;
import com.example.commentservice.pojo.VO.CommentTree;
import com.example.commentservice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentDao commentDao;

//    构建层级评论结构
    @Override
    public List<CommentTree> getCommentTree(Long articleId) {
//        数据库查询的所有评论，根据articleId的
        List<CommentTree> allComments = commentDao.selectCommentTree(articleId);

        // 创建一个HashMap，用于存储所有评论，键为评论的ID，值为评论对象
        // 这一步是为了后续快速查找评论及其父评论
        Map<Long, CommentTree> commentsMap = new HashMap<>();
        // 遍历所有查询到的评论，将它们按ID放入Map中
        for (CommentTree comment : allComments) {
            commentsMap.put(comment.getId(), comment);
        }
        System.out.println("下面是建立的Map索引commentsMap---------------");
        for (Map.Entry<Long, CommentTree> entry : commentsMap.entrySet()) {
            System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
        }
        // 创建一个新的ArrayList，用于存放顶级评论（即父评论ID为null的评论）
        List<CommentTree> topLevelComments = new ArrayList<>();

        // 再次遍历所有评论，目的是构建评论的层级结构
        for (CommentTree comment : allComments) {
//            设置 评论回复窗口显示 的默认值
            comment.setReplyWindowVisible(false);

            // 如果当前评论的父评论ID为null，说明它是一个顶级评论
            if (comment.getParentId() == null) {
//                给顶级评论属性赋值
                comment.setFlagTopComment(1);
                // 将顶级评论加入到topLevelComments列表中
                topLevelComments.add(comment);
            } else {
                // 否则，根据当前评论的父评论ID从Map中查找其父评论
//                注意CommentTree parentComment只是引用，虽然每次都被重新定义，但它内部保存的值是上面已经存在的对象地址
                CommentTree parentComment = commentsMap.get(comment.getParentId());
                // 如果找到了父评论
                if (parentComment != null) {
                    // 初始化父评论的子评论列表，如果还不存在的话
                    if (parentComment.getReplies() == null) {
                        parentComment.setReplies(new ArrayList<>());
                    }
//                    将父评论的用户信息塞入子评论内
                    comment.setParentName(parentComment.getUserName());
//                    判断父评论是否为顶级评论
                    if (parentComment.getFlagTopComment() != null){
                        if (parentComment.getFlagTopComment() == 1){
                            comment.setFlagTopComment(2);
                        }
                    }

                    // 将当前评论添加到其父评论的子评论列表中
                    parentComment.getReplies().add(comment);
                }
            }
        }


        return topLevelComments;
    }
}
