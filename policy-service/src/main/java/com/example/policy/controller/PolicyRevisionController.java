package com.example.policy.controller;

import com.example.policy.response.AjaxResult;
import com.example.policy.response.PageResult;
import com.example.policy.service.PolicyRevisionService;
import com.example.policy.service.PolicyReviewService;
import com.example.policy.vo.DeptReviewDetailVo;
import com.example.policy.vo.PolicyRevisionAddVo;
import com.example.policy.vo.PolicyRevisionDetailVo;
import com.example.policy.vo.PolicyRevisionListVo;
import com.example.policy.vo.RevisionProgressVo;
import com.example.policy.vo.ReviewSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Policy revision controller / 制度修订Controller
 * @author Z1nk
 */
@RestController
@RequestMapping("/revisions")
public class PolicyRevisionController {

    @Autowired
    private PolicyRevisionService revisionService;

    @Autowired
    private PolicyReviewService reviewService;

    @Autowired
    private com.example.policy.service.PolicyDeptRelationService deptRelationService;

    /**
     * Create revision from version / 基于指定版本创建新修订
     */
    @PostMapping("/from-version")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AjaxResult<Long> createRevisionFromVersion(
            @RequestParam Long policyId,
            @RequestParam String baseVersion,
            @RequestParam(defaultValue = "minor") String revisionType) {
        try {
            Long result = revisionService.createRevisionFromVersion(policyId, baseVersion, revisionType);
            return new AjaxResult<Long>(result);
        } catch (RuntimeException e) {
            return AjaxResult.<Long>failedWithType(e.getMessage());
        }
    }

    /**
     * Create revision / 发起修订
     */
    @PostMapping
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AjaxResult<Long> add(@RequestBody PolicyRevisionAddVo revisionAddVo) {
        try {
            Long result = revisionService.add(revisionAddVo);
            return new AjaxResult<Long>(result);
        } catch (RuntimeException e) {
            return AjaxResult.<Long>failedWithType(e.getMessage());
        }
    }

    /**
     * Get revision list / 获取修订列表
     */
    @GetMapping
    public AjaxResult<PageResult<PolicyRevisionListVo>> getList(
            @RequestParam(required = false) Long policyId,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return AjaxResult.success(revisionService.getList(policyId, pageNum, pageSize));
    }

    /**
     * Get revision detail / 获取修订详情
     */
    @GetMapping("/{revisionId}")
    @SuppressWarnings("unchecked")
    public AjaxResult<PolicyRevisionDetailVo> getDetail(@PathVariable Long revisionId) {
        PolicyRevisionDetailVo detail = revisionService.getDetail(revisionId);
        if (detail == null) {
            return AjaxResult.<PolicyRevisionDetailVo>failedWithType("Revision not found / 修订不存在");
        }
        return new AjaxResult<PolicyRevisionDetailVo>(detail);
    }

    /**
     * Edit revision (only draft status) / 编辑修订（仅草稿状态）
     */
    @PutMapping("/{revisionId}")
    @SuppressWarnings("unchecked")
    public AjaxResult<Boolean> edit(@PathVariable Long revisionId, @RequestBody PolicyRevisionAddVo revisionAddVo) {
        try {
            boolean result = revisionService.edit(revisionId, revisionAddVo);
            return new AjaxResult<Boolean>(result);
        } catch (RuntimeException e) {
            return AjaxResult.<Boolean>failedWithType(e.getMessage());
        }
    }

    /**
     * Delete revision (only draft status) / 删除修订（仅草稿状态）
     */
    @DeleteMapping("/{revisionId}")
    @SuppressWarnings("unchecked")
    public AjaxResult<Boolean> del(@PathVariable Long revisionId) {
        try {
            boolean result = revisionService.del(revisionId);
            return new AjaxResult<Boolean>(result);
        } catch (RuntimeException e) {
            return AjaxResult.<Boolean>failedWithType(e.getMessage());
        }
    }

    /**
     * Submit for review (draft -> pending review) / 提交审议（草稿 -> 待审议）
     */
    @PostMapping("/{revisionId}/submit")
    @SuppressWarnings("unchecked")
    public AjaxResult<Boolean> submitForReview(@PathVariable Long revisionId) {
        try {
            boolean result = revisionService.submitForReview(revisionId);
            return new AjaxResult<Boolean>(result);
        } catch (RuntimeException e) {
            return AjaxResult.<Boolean>failedWithType(e.getMessage());
        }
    }

    /**
     * Make effective (approved -> effective) / 生效修订（已通过 -> 已生效）
     */
    @PostMapping("/{revisionId}/make-effective")
    @SuppressWarnings("unchecked")
    public AjaxResult<Boolean> makeEffective(@PathVariable Long revisionId) {
        try {
            boolean result = revisionService.makeEffective(revisionId);
            return new AjaxResult<Boolean>(result);
        } catch (RuntimeException e) {
            return AjaxResult.<Boolean>failedWithType(e.getMessage());
        }
    }

    /**
     * Get revision progress (core visualization data) / 获取审议进度（核心可视化数据）
     */
    @GetMapping("/{revisionId}/progress")
    @SuppressWarnings("unchecked")
    public AjaxResult<RevisionProgressVo> getProgress(@PathVariable Long revisionId) {
        try {
            RevisionProgressVo progress = revisionService.getProgress(revisionId);
            return new AjaxResult<RevisionProgressVo>(progress);
        } catch (RuntimeException e) {
            return AjaxResult.<RevisionProgressVo>failedWithType(e.getMessage());
        }
    }

    /**
     * Get review list for revision / 获取修订的审议记录列表
     */
    @GetMapping("/{revisionId}/reviews")
    public AjaxResult<List<DeptReviewDetailVo>> getReviewList(@PathVariable Long revisionId) {
        List<DeptReviewDetailVo> reviews = reviewService.getReviewList(revisionId);
        return AjaxResult.success(reviews);
    }

    /**
     * Submit review opinion (department operation) / 提交审议意见（部门操作）
     */
    @PostMapping("/{revisionId}/reviews")
    @SuppressWarnings("unchecked")
    public AjaxResult<Boolean> submitReview(@PathVariable Long revisionId, @RequestBody ReviewSubmitVo reviewSubmitVo) {
        try {
            boolean result = reviewService.submitReview(revisionId, reviewSubmitVo);
            return new AjaxResult<Boolean>(result);
        } catch (RuntimeException e) {
            return AjaxResult.<Boolean>failedWithType(e.getMessage());
        }
    }

    /**
     * Get opinion message / 获取意见消息
     */
    private String getOpinionMessage(Integer opinion) {
        switch (opinion) {
            case 1: return "Submitted agree opinion / 已提交同意意见";
            case 2: return "Submitted disagree opinion / 已提交不同意意见";
            case 3: return "Submitted modification suggestion / 已提交修改建议";
            default: return "Submitted review opinion / 已提交审议意见";
        }
    }

    /**
     * Get revisions by policy ID / 获取制度的修订列表（便捷接口）
     */
    @GetMapping("/policy/{policyId}")
    public AjaxResult<PageResult<PolicyRevisionListVo>> getRevisionsByPolicy(
            @PathVariable Long policyId,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return AjaxResult.success(revisionService.getList(policyId, pageNum, pageSize));
    }

    /**
     * Get departments for revision / 获取修订的参与部门列表
     */
    @GetMapping("/{revisionId}/departments")
    public AjaxResult<List<com.example.policy.domain.PolicyDeptRelation>> getDepartments(@PathVariable Long revisionId) {
        List<com.example.policy.domain.PolicyDeptRelation> departments = deptRelationService.getDepartments(revisionId);
        return AjaxResult.success(departments);
    }

    /**
     * Add department to revision / 添加部门到修订（仅草稿状态）
     */
    @PostMapping("/{revisionId}/departments")
    @SuppressWarnings("unchecked")
    public AjaxResult<Boolean> addDepartment(
            @PathVariable Long revisionId,
            @RequestParam Long deptId,
            @RequestParam(defaultValue = "1") Integer isRequired) {
        try {
            return new AjaxResult<Boolean>(deptRelationService.addDepartment(revisionId, deptId, isRequired));
        } catch (RuntimeException e) {
            return AjaxResult.<Boolean>failedWithType(e.getMessage());
        }
    }

    /**
     * Remove department from revision / 从修订移除部门（仅草稿状态）
     */
    @DeleteMapping("/{revisionId}/departments/{deptId}")
    @SuppressWarnings("unchecked")
    public AjaxResult<Boolean> removeDepartment(@PathVariable Long revisionId, @PathVariable Long deptId) {
        try {
            return new AjaxResult<Boolean>(deptRelationService.removeDepartment(revisionId, deptId));
        } catch (RuntimeException e) {
            return AjaxResult.<Boolean>failedWithType(e.getMessage());
        }
    }
}