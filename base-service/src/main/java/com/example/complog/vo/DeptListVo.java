package com.example.complog.vo;

import lombok.Data;

/**
 * Department list view.
 */
@Data
public class DeptListVo {
    private Long id;
    private String name;
    private Long parentId;
}
