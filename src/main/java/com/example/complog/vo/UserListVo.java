package com.example.complog.vo;

import lombok.Data;

/**
 * User list view.
 */
@Data
public class UserListVo {
    private Long id;
    private String name;
    private String phone;
    private Long deptId;
    private String deptName;
    private String role;
    private Integer status;
}
