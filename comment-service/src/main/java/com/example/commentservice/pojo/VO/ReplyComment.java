package com.example.commentservice.pojo.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class ReplyComment {


    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long id;
    private String content;

    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long articleId;

    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long userId;
    private String userName;
    private String avatarUrl;

    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long parentId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;



}
