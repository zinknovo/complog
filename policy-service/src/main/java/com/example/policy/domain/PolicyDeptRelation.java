package com.example.policy.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * Policy department relation domain / 制度部门关联实体
 * Used to record which departments need to participate in the review / 用于记录哪些部门需要参与审议
 */
@TableName(value = "policy_dept_relation")
@Data
public class PolicyDeptRelation {
    /**
     * Primary key ID / 主键ID
     */
    @TableId
    private Long id;

    /**
     * Revision ID / 修订ID
     */
    private Long revisionId;

    /**
     * Related department ID / 关联部门ID
     */
    private Long deptId;

    /**
     * Is required review: 1-required, 0-optional / 是否必须审议：1-必须，0-可选
     */
    private Integer isRequired;

    /**
     * Created time / 创建时间
     */
    private Date createdAt;

    /**
     * Is deleted: 1-deleted, 0-not deleted / 是否删除：1-已删除，0-未删除
     */
    private Integer isDeleted;
}