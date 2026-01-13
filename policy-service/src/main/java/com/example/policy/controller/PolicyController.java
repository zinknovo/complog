package com.example.policy.controller;

import com.example.policy.response.AjaxResult;
import com.example.policy.response.PageResult;
import com.example.policy.service.PolicyService;
import com.example.policy.vo.PolicyAddVo;
import com.example.policy.vo.PolicyDetailVo;
import com.example.policy.vo.PolicyEditVo;
import com.example.policy.vo.PolicyListVo;
import com.example.policy.vo.PolicyVersionHistoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Policy controller / 制度管理Controller
 * @author Z1nk
 */
@RestController
@RequestMapping("/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    /**
     * Create policy / 创建制度
     */
    @PostMapping
    @SuppressWarnings("unchecked")
    public AjaxResult<Boolean> add(@RequestBody PolicyAddVo policyAddVo) {
        try {
            boolean result = policyService.add(policyAddVo);
            return new AjaxResult<Boolean>(result);
        } catch (RuntimeException e) {
            return AjaxResult.<Boolean>failedWithType(e.getMessage());
        }
    }

    /**
     * Edit policy / 编辑制度
     */
    @PutMapping("/{id}")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AjaxResult<Boolean> edit(@PathVariable Long id, @RequestBody PolicyEditVo policyEditVo) {
        try {
            if (policyEditVo.getId() == null) {
                policyEditVo.setId(id);
            }
            boolean result = policyService.edit(policyEditVo);
            return new AjaxResult<Boolean>(result);
        } catch (RuntimeException e) {
            return AjaxResult.<Boolean>failedWithType(e.getMessage());
        }
    }

    /**
     * Delete policy / 删除制度
     */
    @DeleteMapping("/{id}")
    @SuppressWarnings("unchecked")
    public AjaxResult<Boolean> del(@PathVariable Long id) {
        try {
            boolean result = policyService.del(id);
            return new AjaxResult<Boolean>(result);
        } catch (RuntimeException e) {
            return AjaxResult.<Boolean>failedWithType(e.getMessage());
        }
    }

    /**
     * Get policy list / 获取制度列表
     */
    @GetMapping
    public AjaxResult<PageResult<PolicyListVo>> getList(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return AjaxResult.success(policyService.getList(pageNum, pageSize, name, type, status));
    }

    /**
     * Get policy detail / 获取制度详情
     */
    @GetMapping("/{id}")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AjaxResult<PolicyDetailVo> getDetail(@PathVariable Long id) {
        PolicyDetailVo detail = policyService.getDetail(id);
        if (detail == null) {
            return AjaxResult.<PolicyDetailVo>failedWithType("Policy not found / 制度不存在");
        }
        return new AjaxResult<PolicyDetailVo>(detail);
    }

    /**
     * Get policy version history / 获取制度版本历史
     */
    @GetMapping("/{policyId}/versions")
    public AjaxResult<List<PolicyVersionHistoryVo>> getVersionHistory(@PathVariable Long policyId) {
        List<PolicyVersionHistoryVo> history = policyService.getVersionHistory(policyId);
        return AjaxResult.success(history);
    }
}