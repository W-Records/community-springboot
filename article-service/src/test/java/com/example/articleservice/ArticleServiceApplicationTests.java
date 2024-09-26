package com.example.articleservice;

import com.example.articleservice.dao.ArticleCategoryDao;
import com.example.articleservice.dao.ArticleDao;
import com.example.articleservice.pojo.Article;
import com.example.articleservice.pojo.VO.ArticleMapES;
import com.example.articleservice.service.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class ArticleServiceApplicationTests {

    @Autowired
    ArticleDao articleDao;
    @Autowired
    ArticleCategoryDao articleCategoryDao;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    FileService fileService;

    @Test
    void contextLoads() {

        Article article = new Article(null, "java", "Java内容分享", "待更新...", 0, 0, LocalDateTime.now(), null, "已发布", 222L, 1777251906167627778L);
        articleDao.insert(article);
        System.out.println( LocalDateTime.now() );
        System.out.println( article );
        System.out.println( articleDao.selectList(null) );

    }

    @Test
    void test01() {

//        redisTemplate.opsForValue().set("previousURL","");
        String previousURL = (String) redisTemplate.opsForValue().get("previousURL");
        System.out.println( previousURL );

        // 删除字符串
//        redisTemplate.delete("previousURL");

    }

    @Test
    void test02() {
        Integer CurrentPage = 2;
        Integer startIndex = (CurrentPage - 1) * 10;
        System.out.println(articleDao.selectArticleHomeDisplay(startIndex));
//        System.out.println(articleCategoryDao.selectList(null));

    }

//    测试es
    @Test
    void test03() throws IOException {

        //        初始化客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(HttpHost.create("http://192.168.229.133:9200"))
        );

        // 创建SearchRequest对象，指定索引名
        SearchRequest searchRequest = new SearchRequest("my_juejin_article");

        // 设置查询条件为匹配所有
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(new MatchAllQueryBuilder());
        searchRequest.source(sourceBuilder);

        // 执行查询并获取响应
        System.out.println(restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT));


    }
//    获取自动补全建议
    @Test
    void test04() throws IOException {

        //        初始化客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(HttpHost.create("http://192.168.229.133:9200"))
        );

        // 构建SuggestBuilder
        CompletionSuggestionBuilder suggestionBuilder =
                SuggestBuilders.completionSuggestion("suggestion")
                        .prefix("流", Fuzziness.ZERO)
                        .skipDuplicates(true)
                        .size(10);

        // 将SuggestBuilder添加到SearchRequest中
        SearchRequest searchRequest = new SearchRequest("my_juejin_article");
        searchRequest.source().suggest(new SuggestBuilder().addSuggestion("title_suggest", suggestionBuilder));

        // 执行查询
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 处理响应并提取建议
        CompletionSuggestion completionSuggestion = searchResponse.getSuggest().getSuggestion("title_suggest");
        System.out.println(
                completionSuggestion.getEntries().stream()
                .flatMap(entry -> entry.getOptions().stream())
                .map(CompletionSuggestion.Entry.Option::getText)
                .map(Text::toString)
                .collect(Collectors.toList())
        );


    }
//批量导入数据到es
    @Test
    void test05() throws IOException {
        //        初始化客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(HttpHost.create("http://192.168.229.134:9200"))
        );

//        给suggestion属性赋值
//        System.out.println(articleDao.selectArticleMapES());
        List<ArticleMapES> articleMapESList = articleDao.selectArticleMapES();
        System.out.println("没有给suggestion属性赋值："+articleMapESList);
        articleMapESList.forEach(articleMapES -> {
            articleMapES.setSuggestion(fileService.SplitIntoWords(articleMapES.getTitle()));
            System.out.println(articleMapES);
        });
        System.out.println("给suggestion属性赋值："+articleMapESList);
//        List<String> splitIntoWords = fileService.SplitIntoWords("体育新闻鉴赏");


//        批量添加数据到es
        ObjectMapper objectMapper = new ObjectMapper();
        BulkRequest bulkRequest = new BulkRequest();

        for (ArticleMapES article : articleMapESList) {
            IndexRequest indexRequest = new IndexRequest("my_juejin_article")
                    .id(article.getArticleId().toString())
                    .source(objectMapper.writeValueAsString(article), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        if (bulkResponse.hasFailures()) {
            // 处理失败的请求
            System.err.println("批量数据_添加失败：Bulk operation had failures: " + bulkResponse.buildFailureMessage());
        } else {
            System.out.println("批量数据_添加成功：Bulk operation successful.");
        }

    }
//    词条提取
    @Test
    void test0555() {

        //        初始化客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(HttpHost.create("http://192.168.229.133:9200"))
        );

        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer("my_juejin_article", "ik_smart", "体育新闻鉴赏");
        AnalyzeResponse response = null;
        try {
            response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.getTokens().stream()
                .map(AnalyzeResponse.AnalyzeToken::getTerm)
                .collect(Collectors.toList()));

    }
//    测试es分析器-通义 垃圾处理不了这个问题
    @Test
    void test06() {
        //        初始化客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(HttpHost.create("http://192.168.229.133:9200"))
        );

//        帮我将下面的Elasticsearch语句转换为Java代码：
//        GET /my_juejin_article/_analyze
//        {
//            "analyzer": "ik_smart",
//                "text": "学习编程语言"
//        }
        // 构建分析请求
//        AnalyzeRequest analyzeRequest = AnalyzeRequest.buildCustom()
//                .index("my_juejin_article") // 指定索引，如果不需要基于特定索引的分析器，可以不设置或设置为null
//                .analyzer("ik_smart") // 使用ik_smart分析器
//                .text("学习编程语言") // 需要分析的文本
//                .build();
//
//        // 执行分析请求
//        AnalyzeResponse analyzeResponse = restHighLevelClient.indices().analyze(analyzeRequest, RequestOptions.DEFAULT);
//
//        // 处理响应
//        List<AnalyzeResponse.AnalyzeToken> tokens = analyzeResponse.getTokens();
//        for (AnalyzeResponse.AnalyzeToken token : tokens) {
//            System.out.printf(
//                    "Token: [%s], Start Offset: %d, End Offset: %d, Type: %s%n",
//                    token.getTerm(), token.getStartOffset(), token.getEndOffset(), token.getType());
//        }


    }
    //    测试es分析器-gpt 可以处理这个问题
    @Test
    void test07() throws IOException {

        //        初始化客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(HttpHost.create("http://192.168.229.133:9200"))
        );
//        AnalyzeRequest analyzeRequest = AnalyzeRequest.withIndexAnalyzer("my_juejin_article", "ik_smart", "text");
//        AnalyzeResponse analyzeResponse = restHighLevelClient.indices().analyze(analyzeRequest, RequestOptions.DEFAULT);
//
//        System.out.println(analyzeResponse.getTokens().stream()
//                .map(AnalyzeResponse.AnalyzeToken::getTerm)
//                .collect(Collectors.toList()));

        // 创建分析请求
        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer("my_juejin_article", "ik_smart", "体育新闻鉴赏");

        // 执行请求
        AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);

        // 处理响应
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            System.out.println("Token: " + token.getTerm() + ", Start Offset: " + token.getStartOffset() + ", End Offset: " + token.getEndOffset());
        }


    }


}
