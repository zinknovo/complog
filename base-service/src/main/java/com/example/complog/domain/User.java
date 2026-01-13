package com.example.complog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

@TableName(value = "`user`")
@Data
public class User {
    @TableId
    private Long id;

    private String name;

    private String phone;

    private Long deptId;

    private String role;

    private Integer status;

    private Date createdAt;

    private Date updatedAt;

    private Integer isDeleted;
}
