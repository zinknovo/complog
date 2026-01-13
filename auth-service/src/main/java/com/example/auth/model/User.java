package com.example.auth.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 用户实体
 */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String phone;
    private String email;
    private String password; // 密码（不返回给前端）
    private Long deptId;
    private String role; // user, admin
    private Integer status; // 1-正常，0-禁用
    private Date createdAt;
    private Date updatedAt;
    private Integer isDeleted;
}