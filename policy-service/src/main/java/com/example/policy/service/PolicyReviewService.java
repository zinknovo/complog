package com.example.policy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.policy.domain.PolicyReview;
import com.example.policy.vo.DeptReviewDetailVo;
import com.example.policy.vo.ReviewSubmitVo;

import java.util.List;

/**
 * Policy review service / 制度审议服务
 */
public interface PolicyReviewService extends IService<PolicyReview> {
    /**
     * Submit review opinion / 提交审议意见
     * @param revisionId Revision ID / 修订ID
     * @param reviewSubmitVo Review submit VO / 提交审议意见VO
     * @return Success or not / 是否成功
     */
    boolean submitReview(Long revisionId, ReviewSubmitVo reviewSubmitVo);

    /**
     * Get review list for revision / 获取修订的审议记录列表
     * @param revisionId Revision ID / 修订ID
     * @return Review list / 审议列表
     */
    List<DeptReviewDetailVo> getReviewList(Long revisionId);

    /**
     * Check and update revision status automatically / 检查并自动更新修订状态
     * @param revisionId Revision ID / 修订ID
     */
    void checkAndUpdateRevisionStatus(Long revisionId);
}