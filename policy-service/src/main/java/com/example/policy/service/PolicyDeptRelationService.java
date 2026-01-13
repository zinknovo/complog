package com.example.policy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.policy.domain.PolicyDeptRelation;

import java.util.List;

/**
 * Policy department relation service / 制度部门关联服务
 */
public interface PolicyDeptRelationService extends IService<PolicyDeptRelation> {
    /**
     * Add department to revision / 添加部门到修订
     * @param revisionId Revision ID / 修订ID
     * @param deptId Department ID / 部门ID
     * @param isRequired Is required review / 是否必审
     * @return Success or not / 是否成功
     */
    boolean addDepartment(Long revisionId, Long deptId, Integer isRequired);

    /**
     * Remove department from revision / 从修订移除部门
     * @param revisionId Revision ID / 修订ID
     * @param deptId Department ID / 部门ID
     * @return Success or not / 是否成功
     */
    boolean removeDepartment(Long revisionId, Long deptId);

    /**
     * Get departments for revision / 获取修订的部门列表
     * @param revisionId Revision ID / 修订ID
     * @return Department relation list / 部门关联列表
     */
    List<PolicyDeptRelation> getDepartments(Long revisionId);

    /**
     * Batch add departments to revision / 批量添加部门到修订
     * @param revisionId Revision ID / 修订ID
     * @param deptIds Department ID list / 部门ID列表
     * @param isRequired Is required review / 是否必审
     * @return Success or not / 是否成功
     */
    boolean batchAddDepartments(Long revisionId, List<Long> deptIds, Integer isRequired);
}