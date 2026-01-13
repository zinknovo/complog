package com.example.policy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.policy.domain.Policy;
import com.example.policy.response.PageResult;
import com.example.policy.vo.PolicyAddVo;
import com.example.policy.vo.PolicyDetailVo;
import com.example.policy.vo.PolicyEditVo;
import com.example.policy.vo.PolicyListVo;
import com.example.policy.vo.PolicyVersionHistoryVo;

import java.util.List;

/**
 * Policy service / 制度服务
 * @author Z1nk
 * @description Database operations service for policy table / 针对表【policy】的数据库操作Service
 */
public interface PolicyService extends IService<Policy> {
    /**
     * Add policy / 创建制度
     * @param policyAddVo Policy add VO / 创建制度VO
     * @return Success or not / 是否成功
     */
    boolean add(PolicyAddVo policyAddVo);

    /**
     * Edit policy / 编辑制度
     * @param policyEditVo Policy edit VO / 编辑制度VO
     * @return Success or not / 是否成功
     */
    boolean edit(PolicyEditVo policyEditVo);

    /**
     * Delete policy / 删除制度
     * @param id Policy ID / 制度ID
     * @return Success or not / 是否成功
     */
    boolean del(Long id);

    /**
     * Get policy list / 获取制度列表
     * @param pageNum Page number / 页码
     * @param pageSize Page size / 每页大小
     * @param name Policy name filter / 制度名称筛选
     * @param type Policy type filter / 制度类型筛选
     * @param status Status filter / 状态筛选
     * @return Policy list / 制度列表
     */
    PageResult<PolicyListVo> getList(Integer pageNum, Integer pageSize, String name, Integer type, Integer status);

    /**
     * Get policy detail / 获取制度详情
     * @param id Policy ID / 制度ID
     * @return Policy detail / 制度详情
     */
    PolicyDetailVo getDetail(Long id);

    /**
     * Get policy version history / 获取制度版本历史
     * @param policyId Policy ID / 制度ID
     * @return Version history list / 版本历史列表
     */
    List<PolicyVersionHistoryVo> getVersionHistory(Long policyId);
}