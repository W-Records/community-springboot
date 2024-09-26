package com.example.articleservice.pojo.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleHomeDisplay {

    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long articleId;
    private String title;
    private String summary;
    private Integer likesCount;
    private Integer viewsCount;
    private String coverUrl;
    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long authorId;
    private String userName;

}
