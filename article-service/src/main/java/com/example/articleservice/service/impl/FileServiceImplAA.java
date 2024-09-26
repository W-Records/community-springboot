package com.example.articleservice.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.example.articleservice.service.FileService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
//@ConfigurationProperties(prefix = "key的通用前缀部分")


//实现接口方法，完成文件上传到OSS存储空间中
//上传过程中，调用了OSS中的api，所以需要提前先导入OSS的maven依赖
@Service
public class FileServiceImplAA implements FileService {

//    从application.properties配置文件导入信息
//    格式：@Value("${application.properties配置文件中的key}")
//    application.properties配置文件中的key是可以自己随便定义的，@Value注解会将对应的value赋值给下面的变量
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.secret}")
    private String secret;

    @Value("${aliyun.oss.bucket}")
    private String bucket;


    @Override
    public String Upload(MultipartFile filedata) {

        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写
        String endpoint = this.endpoint;
        // 阿里云账号AccessKey
        String accessKeyId = this.accessKeyId;
        String accessKeySecret = this.secret;
        // 填写Bucket名称，即对象存储OSS的一块存储地址
        String bucketName = this.bucket;

        try{
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            // 上传文件流
//            获取输入流，用于读取前端传递过来的文件数据，后续上传OSS也是通过流的形式上传
            InputStream inputStream= filedata.getInputStream();
//            下面3行代码：拼接一个不重复的文件名，用于OSS上的文件名
            String fileName=filedata.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            fileName = uuid+fileName;
            //按照当前日期，创建文件夹，将文件数据 上传到创建的文件夹里面
            //  文件夹路径如：2022/03/15/xx.jpg
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            // 将日期格式化为字符串
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String timeUrl = formattedDate;
            fileName = timeUrl+"/"+fileName;

            // 调用oss方法上传到阿里云
//            参数1：上传的地址块Bucket
//            参数2：设置 OSS存储空间中 文件的名字
//            参数3：读取的数据（输入流），表示 是将 这个本地程序读取到的数据 上传到OSS存储空间
            ossClient.putObject(bucketName,fileName,inputStream);

            // 关闭ossclient
            ossClient.shutdown();

            // 上传之后文件路径
            String url="https://"+bucketName+"."+endpoint+"/"+fileName;
//            将文件在网上的访问路径返回，方便前端获取 然后进行文件数据的访问
            return url;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

//    删除OSS服务上面的图片
    @Override
    public void deleteImage(String imageUrl) {
        // 解析出文件名（即OSS上存储的对象键）
        String objectKey = imageUrl.replaceFirst("https://" + bucket + "." + endpoint + "/", "");

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, secret);

        try {
            // 删除OSS上的对象
            ossClient.deleteObject(bucket, objectKey);

            // 关闭OSSClient
            ossClient.shutdown();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete the image from OSS:", e);
        }
    }

//    elasticsearch服务地址
    @Value("${elasticsearch.host}")
    private String ESHost;


//    获取自动补全建议
    @Override
    public List<String> getAutocompleteSuggestions(String searchText) {

        //        初始化客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(HttpHost.create(ESHost))
        );

        // 构建SuggestBuilder
        CompletionSuggestionBuilder suggestionBuilder =
                SuggestBuilders.completionSuggestion("suggestion")
                        .prefix(searchText, Fuzziness.ZERO)
                        .skipDuplicates(true)
                        .size(10);

        // 将SuggestBuilder添加到SearchRequest中
        SearchRequest searchRequest = new SearchRequest("my_juejin_article");
        searchRequest.source().suggest(new SuggestBuilder().addSuggestion("title_suggest", suggestionBuilder));

        // 执行查询
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 处理响应并提取建议
        CompletionSuggestion completionSuggestion = searchResponse.getSuggest().getSuggestion("title_suggest");
//        解析响应结果
        List<String> suggestionList = completionSuggestion.getEntries().stream()
                .flatMap(entry -> entry.getOptions().stream())
                .map(option -> option.getHit().getSourceAsMap().get("title").toString())
                .collect(Collectors.toList());

        System.out.println(suggestionList);


        return suggestionList;
    }

//    将title拆分成一个个词条
    @Override
    public List<String> SplitIntoWords(String MyText) {
        //        初始化客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(HttpHost.create(ESHost))
        );

        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer("my_juejin_article", "ik_smart", MyText);
        AnalyzeResponse response = null;
        try {
            response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> WordsList = response.getTokens().stream()
                .map(AnalyzeResponse.AnalyzeToken::getTerm)
                .collect(Collectors.toList());
        System.out.println(WordsList);

        return WordsList;
    }


}
