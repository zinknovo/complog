package com.example.policy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.policy.domain.Department;
import com.example.policy.domain.Policy;
import com.example.policy.domain.PolicyRevision;
import com.example.policy.mapper.DepartmentMapper;
import com.example.policy.mapper.PolicyMapper;
import com.example.policy.mapper.PolicyRevisionMapper;
import com.example.policy.response.PageResult;
import com.example.policy.service.PolicyFileService;
import com.example.policy.service.PolicyService;
import com.example.policy.vo.PolicyAddVo;
import com.example.policy.vo.PolicyDetailVo;
import com.example.policy.vo.PolicyEditVo;
import com.example.policy.vo.PolicyListVo;
import com.example.policy.vo.PolicyVersionHistoryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Policy service implementation / 制度服务实现
 */
@Service
public class PolicyServiceImpl extends ServiceImpl<PolicyMapper, Policy> implements PolicyService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private PolicyRevisionMapper revisionMapper;

    @Autowired
    private PolicyFileService policyFileService;

    @Override
    @CacheEvict(value = {"policies", "policyList"}, allEntries = true) // Clear cache when adding / 添加时清除缓存
    public boolean add(PolicyAddVo policyAddVo) {
        Policy policy = new Policy();
        BeanUtils.copyProperties(policyAddVo, policy);
        
        // Set default status / 设置默认状态
        if (policy.getStatus() == null) {
            policy.setStatus(0);  // 0-draft / 草稿
        }
        
        // Set default version / 设置默认版本
        if (policy.getCurrentVersion() == null) {
            policy.setCurrentVersion("v1.0.0");
        }
        
        policy.setCreatedAt(new Date());
        policy.setUpdatedAt(new Date());
        policy.setIsDeleted(0);
        
        boolean result = baseMapper.insert(policy) > 0;
        
        // Create initial revision / 创建初始版本修订
        if (result && policy.getId() != null) {
            createInitialRevision(policy);
        }
        
        return result;
    }

    @Override
    @CacheEvict(value = {"policies", "policyList", "policyVersions"}, key = "#policyEditVo.id") // Clear cache when editing / 编辑时清除缓存
    public boolean edit(PolicyEditVo policyEditVo) {
        Policy policy = baseMapper.selectById(policyEditVo.getId());
        if (policy == null) {
            return false;
        }
        
        // Copy properties but preserve version from database / 复制属性但保留数据库中的版本号
        Integer currentVersion = policy.getVersion();
        BeanUtils.copyProperties(policyEditVo, policy);
        policy.setVersion(currentVersion); // Keep version from database for optimistic locking / 保留数据库版本号用于乐观锁
        policy.setUpdatedAt(new Date());
        
        int rows = baseMapper.updateById(policy);
        if (rows == 0) {
            // Optimistic locking conflict: data was modified by another user / 乐观锁冲突：数据已被其他用户修改
            throw new RuntimeException("数据已被他人修改，请刷新后重试 / Data was modified by another user, please refresh and try again");
        }
        
        return true;
    }

    @Override
    @CacheEvict(value = {"policies", "policyList", "policyVersions"}, key = "#id") // Clear cache when deleting / 删除时清除缓存
    public boolean del(Long id) {
        Policy policy = baseMapper.selectById(id);
        if (policy == null) {
            return false;
        }
        
        // Check if there are active revisions / 检查是否有进行中的修订
        QueryWrapper<PolicyRevision> revisionWrapper = new QueryWrapper<>();
        revisionWrapper.eq("policy_id", id)
                       .in("status", 1, 2)  // pending review or reviewing / 待审议或审议中
                       .eq("is_deleted", 0);
        if (revisionMapper.selectCount(revisionWrapper) > 0) {
            return false;  // Cannot delete policy with active revisions / 不能删除有进行中修订的制度
        }
        
        policy.setIsDeleted(1);
        policy.setUpdatedAt(new Date());
        return baseMapper.updateById(policy) > 0;
    }

    @Override
    @Cacheable(value = "policyList", key = "'list_' + #pageNum + '_' + #pageSize + '_' + (#name != null ? #name : '') + '_' + (#type != null ? #type : '') + '_' + (#status != null ? #status : '')") // Cache query results / 缓存查询结果
    public PageResult<PolicyListVo> getList(Integer pageNum, Integer pageSize, String name, Integer type, Integer status) {
        QueryWrapper<Policy> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (type != null) {
            wrapper.eq("type", type);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        
        wrapper.orderByDesc("created_at");
        
        IPage<Policy> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<PolicyListVo> records = new ArrayList<>();
        
        for (Policy policy : page.getRecords()) {
            PolicyListVo vo = new PolicyListVo();
            BeanUtils.copyProperties(policy, vo);
            
            // Convert type to text / 转换类型为文本
            vo.setTypeText(convertTypeToText(policy.getType()));
            
            // Convert status to text / 转换状态为文本
            vo.setStatusText(convertStatusToText(policy.getStatus()));
            
            // Query owner department name / 查询负责部门名称
            if (policy.getOwnerDeptId() != null) {
                Department dept = departmentMapper.selectById(policy.getOwnerDeptId());
                if (dept != null) {
                    vo.setOwnerDeptName(dept.getName());
                }
            }
            
            records.add(vo);
        }
        
        return PageResult.iPageHandle(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    @Override
    @Cacheable(value = "policies", key = "'detail_' + #id") // Cache detail query / 缓存详情查询
    public PolicyDetailVo getDetail(Long id) {
        Policy policy = baseMapper.selectById(id);
        if (policy == null) {
            return null;
        }
        
        PolicyDetailVo vo = new PolicyDetailVo();
        BeanUtils.copyProperties(policy, vo);
        
        // Convert type and status / 转换类型和状态
        vo.setTypeText(convertTypeToText(policy.getType()));
        vo.setStatusText(convertStatusToText(policy.getStatus()));
        
        // Query owner department name / 查询负责部门名称
        if (policy.getOwnerDeptId() != null) {
            Department dept = departmentMapper.selectById(policy.getOwnerDeptId());
            if (dept != null) {
                vo.setOwnerDeptName(dept.getName());
            }
        }
        
        // Read content from file if exists / 如果存在文件路径，从文件读取内容
        if (policy.getContentFilePath() != null && !policy.getContentFilePath().isEmpty()) {
            try {
                vo.setContent(policyFileService.readVersionContent(policy.getContentFilePath()));
            } catch (Exception e) {
                // If file read fails, use summary / 如果文件读取失败，使用摘要
                vo.setContent(policy.getContentSummary());
            }
        } else {
            vo.setContent(policy.getContentSummary());
        }
        
        return vo;
    }

    @Override
    @Cacheable(value = "policyVersions", key = "'versions_' + #policyId") // Cache version history / 缓存版本历史
    public List<PolicyVersionHistoryVo> getVersionHistory(Long policyId) {
        QueryWrapper<PolicyRevision> wrapper = new QueryWrapper<>();
        wrapper.eq("policy_id", policyId)
               .eq("is_deleted", 0)
               .orderByDesc("version_major", "version_minor", "version_patch");
        
        List<PolicyRevision> revisions = revisionMapper.selectList(wrapper);
        List<PolicyVersionHistoryVo> result = new ArrayList<>();
        
        for (PolicyRevision revision : revisions) {
            PolicyVersionHistoryVo vo = new PolicyVersionHistoryVo();
            vo.setRevisionId(revision.getId());
            vo.setVersion(revision.getVersion());
            vo.setTitle(revision.getTitle());
            vo.setStatus(revision.getStatus());
            vo.setStatusText(convertRevisionStatusToText(revision.getStatus()));
            vo.setIsCurrent(revision.getIsCurrent() != null && revision.getIsCurrent());
            vo.setEffectiveAt(revision.getEffectiveAt());
            vo.setCreatedAt(revision.getCreatedAt());
            vo.setRevisionReason(revision.getRevisionReason());
            
            result.add(vo);
        }
        
        return result;
    }

    /**
     * Create initial revision for new policy / 为新制度创建初始版本
     */
    private void createInitialRevision(Policy policy) {
        PolicyRevision revision = new PolicyRevision();
        revision.setPolicyId(policy.getId());
        revision.setVersion("v1.0.0");
        revision.setVersionMajor(1);
        revision.setVersionMinor(0);
        revision.setVersionPatch(0);
        revision.setTitle("Initial version");
        revision.setStatus(5);  // effective / 已生效
        revision.setIsCurrent(true);
        revision.setEffectiveAt(new Date());
        revision.setCreatedAt(new Date());
        revision.setUpdatedAt(new Date());
        revision.setIsDeleted(0);
        revisionMapper.insert(revision);
        
        // Update policy current revision ID / 更新制度的当前修订ID
        policy.setCurrentRevisionId(revision.getId());
        baseMapper.updateById(policy);
    }

    /**
     * Convert type to text / 转换类型为文本
     */
    private String convertTypeToText(Integer type) {
        if (type == null) {
            return "";
        }
        switch (type) {
            case 1: return "Articles of Association / 公司章程";
            case 2: return "Rules of Procedure / 三会议事规则";
            case 3: return "Internal Rules / 内部规章制度";
            case 4: return "Business Guidelines / 业务规则指引";
            default: return "Unknown / 未知";
        }
    }

    /**
     * Convert status to text / 转换状态为文本
     */
    private String convertStatusToText(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 0: return "Draft / 草稿";
            case 1: return "Effective / 生效中";
            case 2: return "Expired / 已失效";
            default: return "Unknown / 未知";
        }
    }

    /**
     * Convert revision status to text / 转换修订状态为文本
     */
    private String convertRevisionStatusToText(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 0: return "Draft / 草稿";
            case 1: return "Pending Review / 待审议";
            case 2: return "Reviewing / 审议中";
            case 3: return "Approved / 已通过";
            case 4: return "Rejected / 已驳回";
            case 5: return "Effective / 已生效";
            default: return "Unknown / 未知";
        }
    }
}