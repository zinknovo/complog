package com.example.complog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

@TableName(value = "department")
@Data
public class Department {
    @TableId
    private Long id;

    private String name;

    private Long parentId;

    private Integer status;

    private Date createdAt;

    private Date updatedAt;

    private Integer isDeleted;
}
