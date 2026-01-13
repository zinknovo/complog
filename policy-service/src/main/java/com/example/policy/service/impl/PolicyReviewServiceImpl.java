package com.example.policy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.policy.domain.Department;
import com.example.policy.domain.PolicyDeptRelation;
import com.example.policy.domain.PolicyRevision;
import com.example.policy.domain.PolicyReview;
import com.example.policy.domain.User;
import com.example.policy.mapper.DepartmentMapper;
import com.example.policy.mapper.PolicyDeptRelationMapper;
import com.example.policy.mapper.PolicyRevisionMapper;
import com.example.policy.mapper.PolicyReviewMapper;
import com.example.policy.mapper.UserMapper;
import com.example.policy.service.PolicyReviewService;
import com.example.policy.vo.DeptReviewDetailVo;
import com.example.policy.vo.ReviewSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Policy review service implementation / 制度审议服务实现
 */
@Service
public class PolicyReviewServiceImpl extends ServiceImpl<PolicyReviewMapper, PolicyReview> 
        implements PolicyReviewService {

    @Autowired
    private PolicyRevisionMapper revisionMapper;

    @Autowired
    private PolicyDeptRelationMapper deptRelationMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public boolean submitReview(Long revisionId, ReviewSubmitVo reviewSubmitVo) {
        // Query revision record / 查询修订记录
        PolicyRevision revision = revisionMapper.selectById(revisionId);
        if (revision == null) {
            return false;
        }

        // Status check: only pending review or reviewing can submit opinion / 状态校验：只有待审议或审议中才能提交意见
        if (revision.getStatus() != 1 && revision.getStatus() != 2) {
            throw new RuntimeException("Current status does not allow submitting review opinion / 当前状态不允许提交审议意见");
        }

        // Update or create review record / 更新或创建审议记录
        PolicyReview review = baseMapper.selectOne(
            new QueryWrapper<PolicyReview>()
                .eq("revision_id", revisionId)
                .eq("dept_id", reviewSubmitVo.getDeptId())
                .eq("is_deleted", 0)
        );

        if (review == null) {
            review = new PolicyReview();
            review.setRevisionId(revisionId);
            review.setDeptId(reviewSubmitVo.getDeptId());
            review.setCreatedAt(new Date());
            review.setIsDeleted(0);
        }
        
        review.setOpinion(reviewSubmitVo.getOpinion());  // 1-agree, 2-disagree, 3-need modification
        review.setReviewComment(reviewSubmitVo.getComment());
        review.setReviewTime(new Date());
        review.setReviewerId(reviewSubmitVo.getReviewerId());
        review.setUpdatedAt(new Date());

        if (review.getId() == null) {
            baseMapper.insert(review);
        } else {
            baseMapper.updateById(review);
        }

        // Check if all departments have reviewed, automatically update revision status / 检查是否所有部门都已审议，自动更新修订状态
        checkAndUpdateRevisionStatus(revisionId);

        return true;
    }

    @Override
    public List<DeptReviewDetailVo> getReviewList(Long revisionId) {
        // Query all related departments / 查询所有关联部门
        List<PolicyDeptRelation> deptRelations = deptRelationMapper.selectList(
            new QueryWrapper<PolicyDeptRelation>()
                .eq("revision_id", revisionId)
                .eq("is_deleted", 0)
        );

        // Query review records / 查询审议记录
        List<PolicyReview> reviews = baseMapper.selectList(
            new QueryWrapper<PolicyReview>()
                .eq("revision_id", revisionId)
                .eq("is_deleted", 0)
        );

        // Create map for quick lookup / 创建快速查找的map
        java.util.Map<Long, PolicyReview> reviewMap = new java.util.HashMap<>();
        for (PolicyReview review : reviews) {
            reviewMap.put(review.getDeptId(), review);
        }

        List<DeptReviewDetailVo> result = new ArrayList<>();

        // Assemble department review details / 组装部门审议详情
        for (PolicyDeptRelation relation : deptRelations) {
            DeptReviewDetailVo vo = new DeptReviewDetailVo();
            vo.setDeptId(relation.getDeptId());
            vo.setIsRequired(relation.getIsRequired() != null && relation.getIsRequired() == 1);

            // Query department name / 查询部门名称
            Department dept = departmentMapper.selectById(relation.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getName());
            }

            // Get review record / 获取审议记录
            PolicyReview review = reviewMap.get(relation.getDeptId());
            if (review != null) {
                vo.setReviewId(review.getId());
                vo.setOpinion(review.getOpinion());
                vo.setReviewComment(review.getReviewComment());
                vo.setReviewTime(review.getReviewTime());
                vo.setReviewerId(review.getReviewerId());

                // Query reviewer name / 查询审议人姓名
                if (review.getReviewerId() != null) {
                    User reviewer = userMapper.selectById(review.getReviewerId());
                    if (reviewer != null) {
                        vo.setReviewerName(reviewer.getName());
                    }
                }
            } else {
                vo.setOpinion(0);  // pending / 待审议
            }

            // Convert opinion to friendly text / 转换意见为友好文本
            OpinionInfo opinionInfo = convertOpinionToFriendly(vo.getOpinion());
            vo.setOpinionText(opinionInfo.text);
            vo.setOpinionColor(opinionInfo.color);
            vo.setStatusIcon(opinionInfo.icon);

            result.add(vo);
        }

        return result;
    }

    @Override
    @Transactional
    public void checkAndUpdateRevisionStatus(Long revisionId) {
        PolicyRevision revision = revisionMapper.selectById(revisionId);
        if (revision == null || revision.getStatus() == 3 || revision.getStatus() == 4 || revision.getStatus() == 5) {
            return;  // Already final state, no need to check / 已经是终态，不需要检查
        }

        // Query all required departments / 查询所有必审部门
        List<PolicyDeptRelation> requiredDepts = deptRelationMapper.selectList(
            new QueryWrapper<PolicyDeptRelation>()
                .eq("revision_id", revisionId)
                .eq("is_required", 1)  // required / 必审
                .eq("is_deleted", 0)
        );

        // Query review records for these departments / 查询这些部门的审议记录
        if (requiredDepts.isEmpty()) {
            return;
        }

        List<Long> requiredDeptIds = new ArrayList<>();
        for (PolicyDeptRelation relation : requiredDepts) {
            requiredDeptIds.add(relation.getDeptId());
        }

        List<PolicyReview> reviews = baseMapper.selectList(
            new QueryWrapper<PolicyReview>()
                .eq("revision_id", revisionId)
                .in("dept_id", requiredDeptIds)
                .eq("is_deleted", 0)
        );

        // Check if all required departments have reviewed (opinion != 0) / 检查所有必审部门是否都已审议（opinion != 0）
        boolean allReviewed = requiredDeptIds.size() == reviews.size() &&
            reviews.stream().allMatch(r -> r.getOpinion() != null && r.getOpinion() != 0);

        if (!allReviewed) {
            // If not all reviewed, but some departments have started review, update to "reviewing" / 如果还没全部审议完，但已经有部门开始审议，更新为"审议中"
            boolean anyReviewed = reviews.stream().anyMatch(r -> r.getOpinion() != null && r.getOpinion() != 0);
            if (anyReviewed && revision.getStatus() == 1) {
                revision.setStatus(2);  // 1 -> 2 (pending review -> reviewing) / 待审议 -> 审议中
                revision.setUpdatedAt(new Date());
                revisionMapper.updateById(revision);
            }
            return;
        }

        // All required departments have reviewed, judge result / 所有必审部门都已审议，判断结果
        boolean allAgreed = reviews.stream().allMatch(r -> r.getOpinion() != null && r.getOpinion() == 1);  // 1 = agree
        boolean anyRejected = reviews.stream().anyMatch(r -> r.getOpinion() != null && (r.getOpinion() == 2 || r.getOpinion() == 3));  // 2 = disagree, 3 = need modification

        // Update revision status / 更新修订状态
        if (anyRejected) {
            // Some department disagreed -> rejected / 有部门不同意 -> 已驳回
            revision.setStatus(4);  // 4 = rejected
        } else if (allAgreed) {
            // All departments agreed -> approved / 所有部门都同意 -> 已通过
            revision.setStatus(3);  // 3 = approved
            revision.setApprovedAt(new Date());
        }

        revision.setUpdatedAt(new Date());
        revisionMapper.updateById(revision);
    }

    /**
     * Convert opinion to friendly text / 转换意见为友好文本
     */
    private OpinionInfo convertOpinionToFriendly(Integer opinion) {
        if (opinion == null) {
            opinion = 0;
        }
        switch (opinion) {
            case 0: return new OpinionInfo("Pending / 待审议", "gray", "clock");
            case 1: return new OpinionInfo("Agree / 同意", "green", "check");
            case 2: return new OpinionInfo("Disagree / 不同意", "red", "close");
            case 3: return new OpinionInfo("Need Modification / 需修改", "orange", "edit");
            default: return new OpinionInfo("Unknown / 未知", "gray", "clock");
        }
    }

    /**
     * Opinion information inner class / 意见信息内部类
     */
    private static class OpinionInfo {
        String text;
        String color;
        String icon;

        OpinionInfo(String text, String color, String icon) {
            this.text = text;
            this.color = color;
            this.icon = icon;
        }
    }
}