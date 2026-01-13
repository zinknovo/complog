package com.example.policy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.policy.domain.Department;
import com.example.policy.domain.Policy;
import com.example.policy.domain.PolicyDeptRelation;
import com.example.policy.domain.PolicyRevision;
import com.example.policy.domain.User;
import com.example.policy.mapper.DepartmentMapper;
import com.example.policy.mapper.PolicyDeptRelationMapper;
import com.example.policy.mapper.PolicyMapper;
import com.example.policy.mapper.PolicyRevisionMapper;
import com.example.policy.mapper.UserMapper;
import com.example.policy.response.PageResult;
import com.example.policy.service.PolicyFileService;
import com.example.policy.service.PolicyRevisionService;
import com.example.policy.vo.DeptReviewDetailVo;
import com.example.policy.vo.PolicyRevisionAddVo;
import com.example.policy.vo.PolicyRevisionDetailVo;
import com.example.policy.vo.PolicyRevisionListVo;
import com.example.policy.vo.RevisionProgressVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Policy revision service implementation / 制度修订服务实现
 */
@Service
public class PolicyRevisionServiceImpl extends ServiceImpl<PolicyRevisionMapper, PolicyRevision> 
        implements PolicyRevisionService {

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private PolicyFileService policyFileService;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PolicyDeptRelationMapper deptRelationMapper;

    @Autowired
    private com.example.policy.service.PolicyReviewService policyReviewService;

    @Autowired
    private com.example.policy.service.PolicyDeptRelationService deptRelationService;

    @Override
    public Long createRevisionFromVersion(Long policyId, String baseVersion, String revisionType) {
        // Find base version / 查找基础版本
        PolicyRevision baseRevision = baseMapper.selectOne(
            new QueryWrapper<PolicyRevision>()
                .eq("policy_id", policyId)
                .eq("version", baseVersion)
                .eq("is_deleted", 0)
        );

        if (baseRevision == null) {
            throw new RuntimeException("Base version does not exist / 基础版本不存在");
        }

        // Generate new version number / 生成新版本号
        String newVersion = generateNextVersion(policyId, revisionType);

        // Create new revision (copy base version content) / 创建新修订（复制基础版本内容）
        PolicyRevision newRevision = new PolicyRevision();
        BeanUtils.copyProperties(baseRevision, newRevision);
        newRevision.setId(null);
        newRevision.setVersion(newVersion);
        parseVersion(newVersion, newRevision);
        newRevision.setParentVersion(baseVersion);
        newRevision.setParentRevisionId(baseRevision.getId());
        newRevision.setStatus(0);  // draft / 草稿
        newRevision.setIsCurrent(false);
        newRevision.setCreatedAt(new Date());
        newRevision.setUpdatedAt(new Date());
        newRevision.setApprovedAt(null);
        newRevision.setEffectiveAt(null);

        // Copy content file (create new file, not reference) / 复制内容文件（创建新文件，而不是引用）
        if (baseRevision.getContentFilePath() != null && !baseRevision.getContentFilePath().isEmpty()) {
            try {
                String baseContent = policyFileService.readVersionContent(baseRevision.getContentFilePath());
                String newFilePath = policyFileService.saveVersionContent(policyId, newVersion, baseContent);
                newRevision.setContentFilePath(newFilePath);
                newRevision.setContentFileSize(baseRevision.getContentFileSize());
            } catch (Exception e) {
                // If file read fails, copy content directly / 如果文件读取失败，直接复制内容
                newRevision.setContent(baseRevision.getContent());
            }
        } else {
            newRevision.setContent(baseRevision.getContent());
        }

        baseMapper.insert(newRevision);
        return newRevision.getId();
    }

    @Override
    @Transactional
    public Long add(PolicyRevisionAddVo revisionAddVo) {
        PolicyRevision revision = new PolicyRevision();
        revision.setPolicyId(revisionAddVo.getPolicyId());
        revision.setTitle(revisionAddVo.getTitle());
        revision.setRevisionReason(revisionAddVo.getRevisionReason());
        revision.setInitiatorId(revisionAddVo.getInitiatorId());
        revision.setInitiatorDeptId(revisionAddVo.getInitiatorDeptId());
        revision.setStatus(0);  // draft / 草稿
        revision.setIsCurrent(false);
        revision.setCreatedAt(new Date());
        revision.setUpdatedAt(new Date());
        revision.setIsDeleted(0);

        // Generate version number / 生成版本号
        String version = generateNextVersion(revisionAddVo.getPolicyId(), 
            revisionAddVo.getRevisionType() != null ? revisionAddVo.getRevisionType() : "minor");
        revision.setVersion(version);
        parseVersion(version, revision);

        // Save content / 保存内容
        if (revisionAddVo.getContent() != null && !revisionAddVo.getContent().isEmpty()) {
            // If content is large (> 100KB), save to file / 如果内容较大（>100KB），保存到文件
            if (revisionAddVo.getContent().length() > 100000) {
                String filePath = policyFileService.saveVersionContent(
                    revisionAddVo.getPolicyId(), version, revisionAddVo.getContent());
                revision.setContentFilePath(filePath);
                revision.setContentFileSize((long) revisionAddVo.getContent().length());
            } else {
                revision.setContent(revisionAddVo.getContent());
            }
        }

        baseMapper.insert(revision);
        
        // Batch add departments if provided / 如果提供了部门列表，批量添加
        if (revisionAddVo.getDeptIds() != null && !revisionAddVo.getDeptIds().isEmpty()) {
            deptRelationService.batchAddDepartments(
                revision.getId(), 
                revisionAddVo.getDeptIds(), 
                revisionAddVo.getIsRequired()
            );
        }
        
        return revision.getId();
    }

    @Override
    public boolean edit(Long revisionId, PolicyRevisionAddVo revisionAddVo) {
        PolicyRevision revision = baseMapper.selectById(revisionId);
        if (revision == null) {
            return false;
        }

        // Only draft status can be edited / 只有草稿状态才能编辑
        if (revision.getStatus() != 0) {
            throw new RuntimeException("Only draft revisions can be edited / 只有草稿状态的修订才能编辑");
        }

        revision.setTitle(revisionAddVo.getTitle());
        revision.setRevisionReason(revisionAddVo.getRevisionReason());
        revision.setUpdatedAt(new Date());

        // Update content / 更新内容
        if (revisionAddVo.getContent() != null) {
            if (revisionAddVo.getContent().length() > 100000) {
                String filePath = policyFileService.saveVersionContent(
                    revision.getPolicyId(), revision.getVersion(), revisionAddVo.getContent());
                revision.setContentFilePath(filePath);
                revision.setContentFileSize((long) revisionAddVo.getContent().length());
                revision.setContent(null);
            } else {
                revision.setContent(revisionAddVo.getContent());
                revision.setContentFilePath(null);
                revision.setContentFileSize(null);
            }
        }

        return baseMapper.updateById(revision) > 0;
    }

    @Override
    public boolean del(Long revisionId) {
        PolicyRevision revision = baseMapper.selectById(revisionId);
        if (revision == null) {
            return false;
        }

        // Only draft status can be deleted / 只有草稿状态才能删除
        if (revision.getStatus() != 0) {
            return false;
        }

        revision.setIsDeleted(1);
        revision.setUpdatedAt(new Date());
        
        // Delete version files / 删除版本文件
        if (revision.getContentFilePath() != null) {
            policyFileService.deleteVersionFiles(revision.getPolicyId(), revision.getVersion());
        }
        
        return baseMapper.updateById(revision) > 0;
    }

    @Override
    @Transactional
    public boolean submitForReview(Long revisionId) {
        PolicyRevision revision = baseMapper.selectById(revisionId);
        if (revision == null) {
            return false;
        }

        // Status check: only draft can be submitted / 状态校验：只有草稿状态才能提交
        if (revision.getStatus() != 0) {
            throw new RuntimeException("Only draft revisions can be submitted / 只有草稿状态的修订才能提交审议");
        }

        // Update status to pending review / 更新状态为待审议
        revision.setStatus(1);  // pending review / 待审议
        revision.setUpdatedAt(new Date());
        baseMapper.updateById(revision);

        // Note: PolicyReview records will be created by PolicyReviewService when departments submit reviews
        // 注意：PolicyReview记录将在部门提交审议时由PolicyReviewService创建

        return true;
    }

    @Override
    @Transactional
    public boolean makeEffective(Long revisionId) {
        PolicyRevision revision = baseMapper.selectById(revisionId);
        if (revision == null || revision.getStatus() != 3) {
            throw new RuntimeException("Only approved revisions can be made effective / 只有已通过的修订才能生效");
        }

        // Update revision status / 更新修订状态
        revision.setStatus(5);  // effective / 已生效
        revision.setIsCurrent(true);
        revision.setEffectiveAt(new Date());
        revision.setUpdatedAt(new Date());
        baseMapper.updateById(revision);

        // Mark other revisions of the same policy as non-current / 将同一制度的其他修订标记为非当前版本
        baseMapper.update(null,
            new UpdateWrapper<PolicyRevision>()
                .set("is_current", false)
                .eq("policy_id", revision.getPolicyId())
                .ne("id", revisionId)
        );

        // Update policy current version information / 更新制度的当前版本信息
        Policy policy = policyMapper.selectById(revision.getPolicyId());
        if (policy != null) {
            policy.setCurrentVersion(revision.getVersion());
            policy.setCurrentRevisionId(revisionId);

            // Update content file path (if using file storage) / 更新内容文件路径（如果使用文件存储）
            if (revision.getContentFilePath() != null) {
                policy.setContentFilePath(revision.getContentFilePath());
            } else {
                // Read content summary from revision / 从修订表读取内容摘要
                String content = revision.getContent();
                if (content != null && content.length() > 500) {
                    policy.setContentSummary(content.substring(0, 500));
                } else {
                    policy.setContentSummary(content);
                }
            }

            policy.setStatus(1);  // effective / 生效中
            policy.setEffectiveDate(new Date());
            policy.setUpdatedAt(new Date());
            policyMapper.updateById(policy);
        }

        return true;
    }

    @Override
    public PageResult<PolicyRevisionListVo> getList(Long policyId, Integer pageNum, Integer pageSize) {
        QueryWrapper<PolicyRevision> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        
        if (policyId != null) {
            wrapper.eq("policy_id", policyId);
        }
        
        wrapper.orderByDesc("version_major", "version_minor", "version_patch");
        
        IPage<PolicyRevision> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<PolicyRevisionListVo> records = new ArrayList<>();

        for (PolicyRevision revision : page.getRecords()) {
            PolicyRevisionListVo vo = assembleRevisionListVo(revision);
            records.add(vo);
        }

        return PageResult.iPageHandle(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    @Override
    public PolicyRevisionDetailVo getDetail(Long revisionId) {
        PolicyRevision revision = baseMapper.selectById(revisionId);
        if (revision == null) {
            return null;
        }

        PolicyRevisionDetailVo vo = new PolicyRevisionDetailVo();
        BeanUtils.copyProperties(revision, vo);

        // Query policy information / 查询制度信息
        Policy policy = policyMapper.selectById(revision.getPolicyId());
        if (policy != null) {
            vo.setPolicyName(policy.getName());
            vo.setPolicyCode(policy.getCode());
        }

        // Convert status / 转换状态
        StatusInfo statusInfo = convertStatusToFriendly(revision.getStatus());
        vo.setStatusText(statusInfo.text);
        vo.setStatusColor(statusInfo.color);
        vo.setStatusDescription(statusInfo.description);

        // Read content / 读取内容
        if (revision.getContentFilePath() != null && !revision.getContentFilePath().isEmpty()) {
            try {
                vo.setContent(policyFileService.readVersionContent(revision.getContentFilePath()));
            } catch (Exception e) {
                vo.setContent(revision.getContent());
            }
        } else {
            vo.setContent(revision.getContent());
        }

        // Query initiator information / 查询发起人信息
        if (revision.getInitiatorId() != null) {
            User initiator = userMapper.selectById(revision.getInitiatorId());
            if (initiator != null) {
                vo.setInitiatorName(initiator.getName());
            }
        }
        if (revision.getInitiatorDeptId() != null) {
            Department dept = departmentMapper.selectById(revision.getInitiatorDeptId());
            if (dept != null) {
                vo.setInitiatorDeptName(dept.getName());
            }
        }

        // Get progress / 获取进度
        vo.setProgress(getProgress(revisionId));

        // Set available actions / 设置可用操作
        vo.setAvailableActions(getAvailableActions(revision.getStatus()));

        return vo;
    }

    @Override
    public RevisionProgressVo getProgress(Long revisionId) {
        PolicyRevision revision = baseMapper.selectById(revisionId);
        if (revision == null) {
            throw new RuntimeException("Revision does not exist / 修订不存在");
        }

        // Query related departments / 查询关联部门
        List<PolicyDeptRelation> deptRelations = deptRelationMapper.selectList(
            new QueryWrapper<PolicyDeptRelation>()
                .eq("revision_id", revisionId)
                .eq("is_deleted", 0)
        );

        // Query review records / 查询审议记录
        List<DeptReviewDetailVo> deptReviews = policyReviewService.getReviewList(revisionId);

        // Assemble progress VO / 组装进度VO
        RevisionProgressVo vo = new RevisionProgressVo();
        vo.setRevisionId(revisionId);

        // Status friendly conversion / 状态友好化转换
        StatusInfo statusInfo = convertStatusToFriendly(revision.getStatus());
        vo.setRevisionStatus(revision.getStatus());
        vo.setRevisionStatusText(statusInfo.text);
        vo.setRevisionStatusColor(statusInfo.color);

        // Calculate progress statistics / 计算进度统计
        calculateProgress(deptRelations, deptReviews, vo);

        // Assemble department review details / 组装部门审议详情
        vo.setDeptReviews(deptReviews);

        // Generate next step hint / 生成下一步提示
        generateNextStepHint(vo, revision);

        // Time information / 时间信息
        vo.setSubmittedAt(revision.getCreatedAt());
        if (deptReviews != null && !deptReviews.isEmpty()) {
            Date lastReview = deptReviews.stream()
                .filter(d -> d.getReviewTime() != null)
                .map(DeptReviewDetailVo::getReviewTime)
                .max(Date::compareTo)
                .orElse(null);
            vo.setLastReviewAt(lastReview);
        }

        return vo;
    }

    @Override
    public String generateNextVersion(Long policyId, String revisionType) {
        // Query current latest version (including all status revisions) / 查询当前最新版本（包括所有状态的修订）
        PolicyRevision latestRevision = baseMapper.selectOne(
            new QueryWrapper<PolicyRevision>()
                .eq("policy_id", policyId)
                .eq("is_deleted", 0)
                .orderByDesc("version_major", "version_minor", "version_patch")
                .last("LIMIT 1")
        );

        int major = 1;
        int minor = 0;
        int patch = 0;

        if (latestRevision != null) {
            major = latestRevision.getVersionMajor() != null ? latestRevision.getVersionMajor() : 1;
            minor = latestRevision.getVersionMinor() != null ? latestRevision.getVersionMinor() : 0;
            patch = latestRevision.getVersionPatch() != null ? latestRevision.getVersionPatch() : 0;
        }

        // Increment version number based on revision type / 根据修订类型递增版本号
        switch (revisionType.toLowerCase()) {
            case "major":  // Major version +1, minor and patch reset / 主版本号+1，次版本号和修订号归零
                major++;
                minor = 0;
                patch = 0;
                break;
            case "minor":  // Minor version +1, patch reset / 次版本号+1，修订号归零
                minor++;
                patch = 0;
                break;
            case "patch":  // Patch version +1 / 修订号+1
            default:
                patch++;
                break;
        }

        return String.format("v%d.%d.%d", major, minor, patch);
    }

    /**
     * Parse version string to numbers / 解析版本号字符串为数字
     */
    private void parseVersion(String version, PolicyRevision revision) {
        // version = "v1.2.3"
        String[] parts = version.replace("v", "").split("\\.");
        revision.setVersionMajor(Integer.parseInt(parts[0]));
        revision.setVersionMinor(parts.length > 1 ? Integer.parseInt(parts[1]) : 0);
        revision.setVersionPatch(parts.length > 2 ? Integer.parseInt(parts[2]) : 0);
    }

    /**
     * Assemble revision list VO / 组装修订列表VO
     */
    private PolicyRevisionListVo assembleRevisionListVo(PolicyRevision revision) {
        PolicyRevisionListVo vo = new PolicyRevisionListVo();
        BeanUtils.copyProperties(revision, vo);

        // Query policy name / 查询制度名称
        Policy policy = policyMapper.selectById(revision.getPolicyId());
        if (policy != null) {
            vo.setPolicyName(policy.getName());
        }

        // Convert status / 转换状态
        StatusInfo statusInfo = convertStatusToFriendly(revision.getStatus());
        vo.setStatusText(statusInfo.text);
        vo.setStatusColor(statusInfo.color);

        // Query initiator information / 查询发起人信息
        if (revision.getInitiatorId() != null) {
            User initiator = userMapper.selectById(revision.getInitiatorId());
            if (initiator != null) {
                vo.setInitiatorName(initiator.getName());
            }
        }
        if (revision.getInitiatorDeptId() != null) {
            Department dept = departmentMapper.selectById(revision.getInitiatorDeptId());
            if (dept != null) {
                vo.setInitiatorDeptName(dept.getName());
            }
        }

        // Calculate progress / 计算进度
        List<PolicyDeptRelation> deptRelations = deptRelationMapper.selectList(
            new QueryWrapper<PolicyDeptRelation>()
                .eq("revision_id", revision.getId())
                .eq("is_deleted", 0)
        );
        List<DeptReviewDetailVo> reviews = policyReviewService.getReviewList(revision.getId());
        
        int total = deptRelations.size();
        int reviewed = (int) reviews.stream().filter(r -> r.getOpinion() != null && r.getOpinion() != 0).count();
        int required = (int) deptRelations.stream().filter(d -> d.getIsRequired() != null && d.getIsRequired() == 1).count();
        
        vo.setProgress(required > 0 ? (int) (reviewed * 100.0 / required) : 0);
        vo.setProgressText(String.format("%d/%d departments completed review", reviewed, total));

        return vo;
    }

    /**
     * Status friendly conversion / 状态友好化转换
     */
    private StatusInfo convertStatusToFriendly(Integer status) {
        switch (status) {
            case 0: return new StatusInfo("Draft / 草稿", "gray", "Revision content is being edited / 修订内容正在编辑中");
            case 1: return new StatusInfo("Pending Review / 待审议", "blue", "Submitted, waiting for departments to start review / 已提交，等待各部门开始审议");
            case 2: return new StatusInfo("Reviewing / 审议中", "orange", "Departments are reviewing / 各部门正在审议中");
            case 3: return new StatusInfo("Approved / 已通过", "green", "All required departments have agreed / 所有必审部门均已同意");
            case 4: return new StatusInfo("Rejected / 已驳回", "red", "Some department disagreed or requested modification / 有部门提出不同意或需修改");
            case 5: return new StatusInfo("Effective / 已生效", "darkgreen", "Revision content has been updated to policy / 修订内容已更新到制度中");
            default: return new StatusInfo("Unknown / 未知", "gray", "");
        }
    }

    /**
     * Calculate progress percentage and statistics / 计算进度百分比和统计信息
     */
    private void calculateProgress(List<PolicyDeptRelation> deptRelations, 
                                   List<DeptReviewDetailVo> reviews,
                                   RevisionProgressVo vo) {
        int total = deptRelations.size();
        int required = (int) deptRelations.stream()
            .filter(d -> d.getIsRequired() != null && d.getIsRequired() == 1)
            .count();
        
        int reviewed = (int) reviews.stream()
            .filter(r -> r.getOpinion() != null && r.getOpinion() != 0)
            .count();
        
        int agreed = (int) reviews.stream()
            .filter(r -> r.getOpinion() != null && r.getOpinion() == 1)
            .count();
        
        int rejected = (int) reviews.stream()
            .filter(r -> r.getOpinion() != null && r.getOpinion() == 2)
            .count();
        
        int needModify = (int) reviews.stream()
            .filter(r -> r.getOpinion() != null && r.getOpinion() == 3)
            .count();

        vo.setTotalDepts(total);
        vo.setRequiredDepts(required);
        vo.setReviewedCount(reviewed);
        vo.setPendingCount(total - reviewed);
        vo.setAgreedCount(agreed);
        vo.setRejectedCount(rejected);
        vo.setNeedModifyCount(needModify);

        // Calculate progress percentage (based on required departments) / 计算进度百分比（基于必审部门）
        if (required > 0) {
            int percent = (int) (reviewed * 100.0 / required);
            vo.setProgressPercent(Math.min(percent, 100));
        } else {
            vo.setProgressPercent(0);
        }

        // Generate progress text / 生成进度文本
        String progressText = String.format(
            "%d/%d departments completed review, %d agreed, %d need modification%s",
            reviewed, total, agreed, needModify,
            rejected > 0 ? ", " + rejected + " disagreed" : ""
        );
        vo.setProgressText(progressText);
    }

    /**
     * Generate next step operation hint / 生成下一步操作提示
     */
    private void generateNextStepHint(RevisionProgressVo vo, PolicyRevision revision) {
        if (revision.getStatus() == 0) {
            vo.setNextStepHint("Can edit content, click 'Submit for Review' when done / 可以编辑内容，完成后点击'提交审议'");
            vo.setCanEdit(true);
            vo.setCanMakeEffective(false);
        } else if (revision.getStatus() == 1 || revision.getStatus() == 2) {
            List<String> pendingDepts = vo.getDeptReviews().stream()
                .filter(d -> (d.getOpinion() == null || d.getOpinion() == 0) && d.getIsRequired())
                .map(DeptReviewDetailVo::getDeptName)
                .collect(Collectors.toList());

            if (pendingDepts.isEmpty()) {
                vo.setNextStepHint("All departments have completed review / 所有部门已完成审议");
            } else {
                vo.setNextStepHint("Waiting for " + String.join(", ", pendingDepts) + " to complete review / 等待 " + String.join("、", pendingDepts) + " 完成审议");
            }
            vo.setCanEdit(false);
            vo.setCanMakeEffective(false);
        } else if (revision.getStatus() == 3) {
            vo.setNextStepHint("Can execute 'Make Effective' operation to update revision content to policy / 可以执行'生效'操作，将修订内容更新到制度中");
            vo.setCanEdit(false);
            vo.setCanMakeEffective(true);
        } else if (revision.getStatus() == 4) {
            vo.setNextStepHint("Revision has been rejected, can view department opinions and resubmit / 修订已被驳回，可以查看各部门意见后重新提交");
            vo.setCanEdit(false);
            vo.setCanMakeEffective(false);
        } else {
            vo.setNextStepHint("Revision is effective / 修订已生效");
            vo.setCanEdit(false);
            vo.setCanMakeEffective(false);
        }
    }

    /**
     * Get available actions based on status / 根据状态获取可用操作
     */
    private List<String> getAvailableActions(Integer status) {
        List<String> actions = new ArrayList<>();
        switch (status) {
            case 0:  // draft
                actions.add("Submit for Review / 提交审议");
                actions.add("Edit Content / 编辑内容");
                break;
            case 1:  // pending review
            case 2:  // reviewing
                actions.add("View Progress / 查看进度");
                break;
            case 3:  // approved
                actions.add("Make Effective / 生效");
                actions.add("View Progress / 查看进度");
                break;
            case 4:  // rejected
                actions.add("View Reviews / 查看审议记录");
                break;
            case 5:  // effective
                actions.add("View Details / 查看详情");
                break;
        }
        return actions;
    }

    /**
     * Status information inner class / 状态信息内部类
     */
    private static class StatusInfo {
        String text;
        String color;
        String description;
        
        StatusInfo(String text, String color, String description) {
            this.text = text;
            this.color = color;
            this.description = description;
        }
    }
}