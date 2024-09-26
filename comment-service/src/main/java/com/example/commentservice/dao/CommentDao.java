package com.example.commentservice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.commentservice.pojo.Comment;
import com.example.commentservice.pojo.VO.CommentTree;
import com.example.commentservice.pojo.VO.ReplyComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentDao extends BaseMapper<Comment> {

//    查询全部评论
    @Select("SELECT c.id, c.content, c.article_id, c.parent_id, c.create_time, u.user_id, u.user_name, u.avatar_url\n" +
            "FROM comments c\n" +
            "JOIN user u\n" +
            "ON c.user_id=u.user_id\n" +
            "WHERE c.article_id = #{id}\n" +
            "ORDER BY c.create_time;")
    public List<CommentTree> selectCommentTree(Long id);


//查询回复
    @Select("SELECT c.*,u.user_name,u.avatar_url\n" +
            "FROM comments c\n" +
            "JOIN user u\n" +
            "ON c.user_id = u.user_id\n" +
            "WHERE parent_id IN(SELECT id FROM comments WHERE user_id =  #{userId}) ")
    public List<ReplyComment> selectReply(Long userId);


//    查询未读评论
    @Select("SELECT count(*)\n" +
            "FROM comments c\n" +
            "JOIN user u\n" +
            "ON c.user_id = u.user_id\n" +
            "WHERE ((parent_id IN(SELECT id FROM comments WHERE user_id =  #{userId})) and c.user_id != #{userId}) and read_or_not=0")
    public Integer selectNotRead(Long userId);

//    查询全部回复，不带他表消息，方便映射
    @Select("SELECT c.*\n" +
            "FROM comments c\n" +
            "JOIN user u\n" +
            "ON c.user_id = u.user_id\n" +
            "WHERE parent_id IN(SELECT id FROM comments WHERE user_id =  #{userId}) ")
    public List<Comment> selectReplyNoOtherTable(Long userId);

//    查询父级评论id，并排除父级评论是自己
    @Select("SELECT * \n" +
            "FROM comments\n" +
            "WHERE (id = #{parentId}) AND (user_id != #{userId})")
    public Comment selectParentId(Long parentId, Long userId);


//    接收文章id，删除此文章下的所有评论
    @Select("DELETE FROM comments WHERE article_id = #{articleId}")
    public void deleteCommentById(Long articleId);
}
