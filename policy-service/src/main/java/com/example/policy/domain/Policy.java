package com.example.policy.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.util.Date;
import lombok.Data;

/**
 * Policy domain / 制度实体
 */
@TableName(value = "policy")
@Data
public class Policy {
    /**
     * Primary key ID / 主键ID
     */
    @TableId
    private Long id;

    /**
     * Policy name / 制度名称
     */
    private String name;

    /**
     * Policy code (unique) / 制度编号（唯一）
     */
    private String code;

    /**
     * Policy type: 1-articles of association, 2-rules of procedure, 3-internal rules, 4-business guidelines
     * 制度类型：1-公司章程，2-三会议事规则，3-内部规章制度，4-业务规则指引
     */
    private Integer type;

    /**
     * Current effective version, e.g. v1.2.0 / 当前生效版本号，如 v1.2.0
     */
    private String currentVersion;

    /**
     * Current effective revision ID / 当前生效的修订ID
     */
    private Long currentRevisionId;

    /**
     * Content summary for list display / 内容摘要（用于列表展示）
     */
    private String contentSummary;

    /**
     * Content file path for current version / 当前生效版本的文件路径
     */
    private String contentFilePath;

    /**
     * Effective date / 生效日期
     */
    private Date effectiveDate;

    /**
     * Expiry date (nullable) / 失效日期（可为空）
     */
    private Date expiryDate;

    /**
     * Status: 0-draft, 1-effective, 2-expired / 状态：0-草稿，1-生效中，2-已失效
     */
    private Integer status;

    /**
     * Owner department ID / 负责部门ID
     */
    private Long ownerDeptId;

    /**
     * Creator / 创建人
     */
    private String creator;

    /**
     * Created time / 创建时间
     */
    private Date createdAt;

    /**
     * Updated time / 更新时间
     */
    private Date updatedAt;

    /**
     * Version for optimistic locking / 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * Updater / 更新人
     */
    private String updater;

    /**
     * Is deleted: 1-deleted, 0-not deleted / 是否删除：1-已删除，0-未删除
     */
    private Integer isDeleted;
}