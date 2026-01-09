package com.example.complog.vo;

import lombok.Data;

/**
 * User create request.
 */
@Data
public class UserAddVo {
    private String name;
    private String phone;
    private Long deptId;
    private String role;
    private Integer status;
}
