package com.example.articleservice.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "articles", autoResultMap = true) // 表名映射
public class Article {

    @TableId(value = "article_id") // 主键映射
    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long articleId;

    private String title;

    private String summary;

    private String content;

    private Integer likesCount;

    private Integer viewsCount;

//    @TableField(fill = FieldFill.INSERT) // 插入时自动填充当前时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;

    private String coverUrl;

    private String status;

//    @TableField(fill = FieldFill.INSERT) // 插入时自动填充当前时间
//    private LocalDateTime createdAt;
//
//    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充当前时间
//    private LocalDateTime updatedAt;

    @TableField("category_id")
    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long categoryId;

    @TableField("author_id")
    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long authorId;

}
