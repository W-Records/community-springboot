package com.example.articleservice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.articleservice.pojo.Article;
import com.example.articleservice.pojo.VO.ArticleDetail;
import com.example.articleservice.pojo.VO.ArticleHomeDisplay;
import com.example.articleservice.pojo.VO.ArticleMapES;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleDao extends BaseMapper<Article> {

    // 查询首页文章列表
    @Select("select a.article_id, a.title, a.summary, a.cover_url, a.likes_count, a.views_count, a.author_id, u.user_name\n" +
            "from articles a JOIN user u\n" +
            "on a.author_id = u.user_id\n" +
            "limit #{startIndex},10;")
    public List<ArticleHomeDisplay> selectArticleHomeDisplay(Integer startIndex);

    // 查询文章总数
    @Select("select count(*) from articles;")
    public Integer selectArticleCount();

//    文章详情页面，返回文章详情数据
    @Select("SELECT a.article_id, a.title, a.content, a.likes_count, a.views_count, a.published_at, a.author_id, u.user_name\n" +
            "FROM articles a JOIN user u\n" +
            "on a.author_id = u.user_id\n" +
            "WHERE a.article_id = #{articleId}")
    public ArticleDetail selectArticleDetailById(Long articleId);

//    与es字段映射
    @Select("select a.article_id, a.title, a.summary, a.cover_url, a.likes_count, a.views_count, a.author_id, u.user_name\n" +
            "from articles a JOIN user u\n" +
            "on a.author_id = u.user_id;")
    public List<ArticleMapES> selectArticleMapES();

    @Select("select a.article_id, a.title, a.summary, a.cover_url, a.likes_count, a.views_count, a.author_id, u.user_name\n" +
            "from articles a JOIN user u\n" +
            "on a.author_id = u.user_id\n" +
            "where a.author_id = #{authorId};")
    List<ArticleHomeDisplay> selectUserArticle(Long authorId);


//    通过文章id删除文章
    @Select("DELETE FROM articles WHERE article_id = #{articleId}")
    void deleteArticleById(Long articleId);

//    修改文章内容
    @Update("UPDATE articles \n" +
            "SET title=#{title},content=#{content},cover_url=#{coverUrl},category_id=#{categoryId},summary=#{summary} \n" +
            "WHERE article_id = #{articleId}")
    void updateArticleByid(Article article);
}
