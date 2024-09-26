package com.example.articleservice.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "article_category", autoResultMap = true) // 表名映射
public class ArticleCategory {

    @TableId(value = "category_id") // 主键映射
    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long categoryId;

    private String categoryName;

}
