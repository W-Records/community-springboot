package com.example.articleservice.controller;

import com.example.articleservice.clients.CommentClient;
import com.example.articleservice.dao.ArticleDao;
import com.example.articleservice.pojo.Article;
import com.example.articleservice.pojo.Result;
import com.example.articleservice.pojo.VO.ArticleDetail;
import com.example.articleservice.pojo.VO.ArticleHomeDisplay;
import com.example.articleservice.pojo.VO.UploadResponse;
import com.example.articleservice.service.FileService;
import com.example.articleservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private FileService fileService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    ArticleDao articleDao;
    @Autowired
    CommentClient commentClient;

//保存文章插入数据库
    @PostMapping("/saveArticle")
    public Result saveArticle(@RequestBody Article article){
        System.out.println("保存文章传递过来的数据：" + article);
        article.setPublishedAt(LocalDateTime.now());
        //获取当前请求的用户信息
        Claims userClaims = jwtUtil.parseJwt(httpServletRequest.getHeader("Authorization"));
        Long userId = userClaims.get("userId", Long.class);
        article.setAuthorId(userId);
        article.setLikesCount(0);
        article.setViewsCount(0);
        articleDao.insert(article);
        return Result.succeed();
    }

//    返回主页列表文章数据展示
    @GetMapping("/getArticleList")
    public Result getArticleList(Integer CurrentPage){
        Integer startIndex = (CurrentPage - 1) * 10;
        List<ArticleHomeDisplay> articleList = articleDao.selectArticleHomeDisplay(startIndex);
        return Result.succeed(articleList);
    }
    // 返回文章总数
    @GetMapping("/getArticleCount")
    public Result getArticleCount(){
        Integer articleCount = articleDao.selectArticleCount();
        return Result.succeed(articleCount);
    }

//    文章详情页面，返回文章详情数据
    @GetMapping("/getArticleDetail")
    public Result getArticleDetail(Long articleId){
        ArticleDetail articleDetail = articleDao.selectArticleDetailById(articleId);
        System.out.println("根据id查出的文章详情："+ articleDetail);
        return Result.succeed(articleDetail);
    }

//    根据文章id获取文章表的数据,更改文章页面 回显用的
    @GetMapping("/getArticleTable")
    public Result getArticleTable(Long articleId){
        Article article = articleDao.selectById(articleId);
        System.out.println("根据id查出的article(更改文章页面)："+ article);
        return Result.succeed(article);
    }

//    获取自动补全建议
    @GetMapping("/getAutocompleteSuggestions")
    public Result getAutocompleteSuggestions(String searchText){
        List<String> autocompleteSuggestions = fileService.getAutocompleteSuggestions(searchText);
        return Result.succeed(autocompleteSuggestions);
    }

//    获取当前用户发布的文章
    @GetMapping("/getUserArticle")
    public Result getUserArticle(Long authorId){
        List<ArticleHomeDisplay> userArticle = articleDao.selectUserArticle(authorId);
        return Result.succeed(userArticle);
    }

//    根据文章id删除当前文章
    @GetMapping("/deleteArticle")
    public Result deleteArticle(Long articleId){
        System.out.println("删除文章传递过来的数据articleId：" + articleId);
        commentClient.deleteCommentById(articleId);

        articleDao.deleteArticleById(articleId);
        return Result.succeed();
    }

//    根据文章id修改文章内容
    @PostMapping("/updateArticle")
    public Result updateArticle(@RequestBody Article article){
        System.out.println("修改文章传递过来的数据：" + article);
        articleDao.updateArticleByid(article);
        return Result.succeed();
    }


//    图片文件上传OSS，返回URL
    @PostMapping("/imageUpload")
    public UploadResponse imageUpload(MultipartFile filedata){

        String url = fileService.Upload(filedata);
        System.out.println("存入OSS的在线URL：" + url);

//        在封面上传，或者头像上传的时候，用户可能不断上传尝试，但是我们只需要最后的图片，之前的图片全部删除
        System.out.println("请求头uploadMark中的数据：" + httpServletRequest.getHeader("uploadMark"));
//        获取当前请求的用户信息，记录他的上传图片记录
        Claims userClaims = jwtUtil.parseJwt(httpServletRequest.getHeader("Authorization"));
        System.out.println("当前用户是：" + userClaims);
        Long userId = userClaims.get("userId", Long.class);
        System.out.println("提取用户的id：" + userId);

        if (httpServletRequest.getHeader("uploadMark")==null||httpServletRequest.getHeader("uploadMark").equals("")){
            return UploadResponse.success(url,null,null);
        }
        if (httpServletRequest.getHeader("uploadMark").equals("single")){
            System.out.println("进到判断里面了，这是 封面上传、头像上传");
            String previousURL = (String) redisTemplate.opsForValue().get("previousURL_"+userId);
            System.out.println("previousURL当前用户历史URL的redis键："+previousURL);
            if (previousURL==null){
                System.out.println("没有此用户的redis，进入到创建模块");
                redisTemplate.opsForValue().set("previousURL_"+userId, url);
                return UploadResponse.success(url,null,null);
            }
            if (!previousURL.equals("")){
                System.out.println("redis存了过往URL");
                fileService.deleteImage(previousURL);
            }
            redisTemplate.opsForValue().set("previousURL_"+userId, url);
        }

        return UploadResponse.success(url,null,null);
    }
//    图片删除
    @PostMapping("/deleteImage")
    public Result deleteImage(@RequestBody List<String> deletedImageUrls){
        System.out.println(deletedImageUrls);
        deletedImageUrls.forEach(fileService::deleteImage);
        return null;
    }
}
