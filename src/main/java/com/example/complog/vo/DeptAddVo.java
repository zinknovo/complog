package com.example.complog.vo;

import lombok.Data;

/**
 * Department create request.
 */
@Data
public class DeptAddVo {
    private String name;
    private Long parentId;
}
