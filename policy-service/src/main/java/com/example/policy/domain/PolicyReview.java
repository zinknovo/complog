package com.example.policy.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * Policy review domain / 制度审议实体
 */
@TableName(value = "policy_review")
@Data
public class PolicyReview {
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
     * Review department ID / 审议部门ID
     */
    private Long deptId;

    /**
     * Reviewer user ID (department head or assigned reviewer) / 审议人ID（部门负责人或指定审议人）
     */
    private Long reviewerId;

    /**
     * Opinion: 0-pending, 1-agree, 2-disagree, 3-need modification
     * 审议意见：0-待审议，1-同意，2-不同意，3-需修改
     */
    private Integer opinion;

    /**
     * Review comment content / 审议意见内容
     */
    private String reviewComment;

    /**
     * Review time / 审议时间
     */
    private Date reviewTime;

    /**
     * Created time / 创建时间
     */
    private Date createdAt;

    /**
     * Updated time / 更新时间
     */
    private Date updatedAt;

    /**
     * Is deleted: 1-deleted, 0-not deleted / 是否删除：1-已删除，0-未删除
     */
    private Integer isDeleted;
}