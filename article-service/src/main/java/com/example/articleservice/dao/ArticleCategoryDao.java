package com.example.articleservice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.articleservice.pojo.Article;
import com.example.articleservice.pojo.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleCategoryDao extends BaseMapper<ArticleCategory> {



}
