package com.example.policy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.policy.domain.PolicyDeptRelation;
import com.example.policy.domain.PolicyRevision;
import com.example.policy.mapper.PolicyDeptRelationMapper;
import com.example.policy.mapper.PolicyRevisionMapper;
import com.example.policy.service.PolicyDeptRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Policy department relation service implementation / 制度部门关联服务实现
 */
@Service
public class PolicyDeptRelationServiceImpl extends ServiceImpl<PolicyDeptRelationMapper, PolicyDeptRelation> 
        implements PolicyDeptRelationService {

    @Autowired
    private PolicyRevisionMapper revisionMapper;

    @Override
    @Transactional
    public boolean addDepartment(Long revisionId, Long deptId, Integer isRequired) {
        // Check revision status, only draft can modify departments / 检查修订状态，只有草稿状态才能修改部门
        PolicyRevision revision = revisionMapper.selectById(revisionId);
        if (revision == null) {
            throw new RuntimeException("Revision does not exist / 修订不存在");
        }
        if (revision.getStatus() != 0) {
            throw new RuntimeException("Only draft revisions can modify departments / 只有草稿状态的修订才能修改部门");
        }

        // Check if relation already exists / 检查关联是否已存在
        PolicyDeptRelation existing = baseMapper.selectOne(
            new QueryWrapper<PolicyDeptRelation>()
                .eq("revision_id", revisionId)
                .eq("dept_id", deptId)
                .eq("is_deleted", 0)
        );

        if (existing != null) {
            return true;  // Already exists / 已存在
        }

        // Create new relation / 创建新关联
        PolicyDeptRelation relation = new PolicyDeptRelation();
        relation.setRevisionId(revisionId);
        relation.setDeptId(deptId);
        relation.setIsRequired(isRequired != null && isRequired == 1 ? 1 : 0);
        relation.setCreatedAt(new Date());
        relation.setIsDeleted(0);

        return baseMapper.insert(relation) > 0;
    }

    @Override
    @Transactional
    public boolean removeDepartment(Long revisionId, Long deptId) {
        // Check revision status / 检查修订状态
        PolicyRevision revision = revisionMapper.selectById(revisionId);
        if (revision == null) {
            throw new RuntimeException("Revision does not exist / 修订不存在");
        }
        if (revision.getStatus() != 0) {
            throw new RuntimeException("Only draft revisions can modify departments / 只有草稿状态的修订才能修改部门");
        }

        // Soft delete relation / 软删除关联
        PolicyDeptRelation relation = baseMapper.selectOne(
            new QueryWrapper<PolicyDeptRelation>()
                .eq("revision_id", revisionId)
                .eq("dept_id", deptId)
                .eq("is_deleted", 0)
        );

        if (relation == null) {
            return false;
        }

        relation.setIsDeleted(1);
        return baseMapper.updateById(relation) > 0;
    }

    @Override
    public List<PolicyDeptRelation> getDepartments(Long revisionId) {
        return baseMapper.selectList(
            new QueryWrapper<PolicyDeptRelation>()
                .eq("revision_id", revisionId)
                .eq("is_deleted", 0)
        );
    }

    @Override
    @Transactional
    public boolean batchAddDepartments(Long revisionId, List<Long> deptIds, Integer isRequired) {
        if (deptIds == null || deptIds.isEmpty()) {
            return true;
        }

        // Check revision status / 检查修订状态
        PolicyRevision revision = revisionMapper.selectById(revisionId);
        if (revision == null) {
            throw new RuntimeException("Revision does not exist / 修订不存在");
        }
        if (revision.getStatus() != 0) {
            throw new RuntimeException("Only draft revisions can modify departments / 只有草稿状态的修订才能修改部门");
        }

        int required = isRequired != null && isRequired == 1 ? 1 : 0;
        Date now = new Date();

        for (Long deptId : deptIds) {
            // Check if already exists / 检查是否已存在
            PolicyDeptRelation existing = baseMapper.selectOne(
                new QueryWrapper<PolicyDeptRelation>()
                    .eq("revision_id", revisionId)
                    .eq("dept_id", deptId)
                    .eq("is_deleted", 0)
            );

            if (existing == null) {
                PolicyDeptRelation relation = new PolicyDeptRelation();
                relation.setRevisionId(revisionId);
                relation.setDeptId(deptId);
                relation.setIsRequired(required);
                relation.setCreatedAt(now);
                relation.setIsDeleted(0);
                baseMapper.insert(relation);
            } else {
                // Update isRequired if needed / 如果需要，更新isRequired
                if (!existing.getIsRequired().equals(required)) {
                    existing.setIsRequired(required);
                    baseMapper.updateById(existing);
                }
            }
        }

        return true;
    }
}