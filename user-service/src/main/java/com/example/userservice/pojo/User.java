package com.example.userservice.pojo;

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
@TableName(value = "user", autoResultMap = true)
public class User {

    @TableId(value = "user_id")
    @JsonSerialize(using = ToStringSerializer.class) // 使用ToStringSerializer将Long转为String输出
    private Long userId;
    private String userName;
    private String userPassword;
    private String avatarUrl;

}
