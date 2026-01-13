package com.example.policy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.policy.domain.PolicyRevision;
import com.example.policy.response.PageResult;
import com.example.policy.vo.PolicyRevisionAddVo;
import com.example.policy.vo.PolicyRevisionDetailVo;
import com.example.policy.vo.PolicyRevisionListVo;
import com.example.policy.vo.RevisionProgressVo;

/**
 * Policy revision service / 制度修订服务
 */
public interface PolicyRevisionService extends IService<PolicyRevision> {
    /**
     * Create revision from version / 基于指定版本创建新修订
     * @param policyId Policy ID / 制度ID
     * @param baseVersion Base version / 基础版本
     * @param revisionType Revision type: major/minor/patch / 修订类型：major/minor/patch
     * @return Revision ID / 修订ID
     */
    Long createRevisionFromVersion(Long policyId, String baseVersion, String revisionType);

    /**
     * Add revision / 创建修订
     * @param revisionAddVo Revision add VO / 创建修订VO
     * @return Revision ID / 修订ID
     */
    Long add(PolicyRevisionAddVo revisionAddVo);

    /**
     * Edit revision (only draft status) / 编辑修订（仅草稿状态）
     * @param revisionId Revision ID / 修订ID
     * @param revisionAddVo Revision add VO / 修订VO
     * @return Success or not / 是否成功
     */
    boolean edit(Long revisionId, PolicyRevisionAddVo revisionAddVo);

    /**
     * Delete revision (only draft status) / 删除修订（仅草稿状态）
     * @param revisionId Revision ID / 修订ID
     * @return Success or not / 是否成功
     */
    boolean del(Long revisionId);

    /**
     * Submit for review (draft -> pending review) / 提交审议（草稿 -> 待审议）
     * @param revisionId Revision ID / 修订ID
     * @return Success or not / 是否成功
     */
    boolean submitForReview(Long revisionId);

    /**
     * Make effective (approved -> effective) / 生效修订（已通过 -> 已生效）
     * @param revisionId Revision ID / 修订ID
     * @return Success or not / 是否成功
     */
    boolean makeEffective(Long revisionId);

    /**
     * Get revision list / 获取修订列表
     * @param policyId Policy ID (optional) / 制度ID（可选）
     * @param pageNum Page number / 页码
     * @param pageSize Page size / 每页大小
     * @return Revision list / 修订列表
     */
    PageResult<PolicyRevisionListVo> getList(Long policyId, Integer pageNum, Integer pageSize);

    /**
     * Get revision detail / 获取修订详情
     * @param revisionId Revision ID / 修订ID
     * @return Revision detail / 修订详情
     */
    PolicyRevisionDetailVo getDetail(Long revisionId);

    /**
     * Get revision progress (core visualization data) / 获取审议进度（核心可视化数据）
     * @param revisionId Revision ID / 修订ID
     * @return Progress data / 进度数据
     */
    RevisionProgressVo getProgress(Long revisionId);

    /**
     * Generate next version number / 生成下一个版本号
     * @param policyId Policy ID / 制度ID
     * @param revisionType Revision type: major/minor/patch / 修订类型：major/minor/patch
     * @return Next version number / 下一个版本号
     */
    String generateNextVersion(Long policyId, String revisionType);
}