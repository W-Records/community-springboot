package com.example.commentservice.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

//返回给前端的评论树结构
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentTree {

    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long id;
    private String content;
    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long articleId;
    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long userId;
    private String userName;
    private String avatarUrl;
    // 父评论信息
    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long parentId;
    private String parentName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

//    标记是否为顶级评论, 1为顶级评论、2为二级评论
    private Integer flagTopComment;

//    前端需要的字段，维护当前评论回复窗口的显示
    private Boolean ReplyWindowVisible;


    private List<CommentTree> replies;



}
