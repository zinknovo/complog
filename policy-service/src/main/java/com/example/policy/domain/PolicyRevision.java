package com.example.policy.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * Policy revision domain / 制度修订实体
 */
@TableName(value = "policy_revision")
@Data
public class PolicyRevision {
    /**
     * Primary key ID / 主键ID
     */
    @TableId
    private Long id;

    /**
     * Policy ID / 关联制度ID
     */
    private Long policyId;

    /**
     * Version number, e.g. v1.0.0 / 版本号，如 v1.0.0
     */
    private String version;

    /**
     * Major version number for sorting / 主版本号（用于排序）
     */
    private Integer versionMajor;

    /**
     * Minor version number / 次版本号
     */
    private Integer versionMinor;

    /**
     * Patch version number / 修订号
     */
    private Integer versionPatch;

    /**
     * Parent version this revision is based on / 基于哪个版本修订
     */
    private String parentVersion;

    /**
     * Parent revision ID / 父修订ID
     */
    private Long parentRevisionId;

    /**
     * Revision title / 修订标题
     */
    private String title;

    /**
     * Revision content (if small text) / 修订内容（如果小文本）
     */
    private String content;

    /**
     * Content file path (if large file) / 内容文件路径（如果大文件）
     */
    private String contentFilePath;

    /**
     * File size in bytes / 文件大小（字节）
     */
    private Long contentFileSize;

    /**
     * Reason for revision / 修订原因
     */
    private String revisionReason;

    /**
     * Status: 0-draft, 1-pending review, 2-reviewing, 3-approved, 4-rejected, 5-effective
     * 状态：0-草稿，1-待审议，2-审议中，3-已通过，4-已驳回，5-已生效
     */
    private Integer status;

    /**
     * Is current effective version / 是否为当前生效版本
     */
    private Boolean isCurrent;

    /**
     * Initiator user ID / 发起人ID
     */
    private Long initiatorId;

    /**
     * Initiator department ID / 发起部门ID
     */
    private Long initiatorDeptId;

    /**
     * Approval time / 通过时间
     */
    private Date approvedAt;

    /**
     * Effective time / 生效时间
     */
    private Date effectiveAt;

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