package com.example.complog.controller.mini;

import com.example.complog.response.AjaxResult;
import com.example.complog.response.PageResult;
import com.example.complog.service.ActivityService;
import com.example.complog.service.RegistrationService;
import com.example.complog.vo.ActivityDetailVo;
import com.example.complog.vo.UserSignDaysVo;
import com.example.complog.vo.UserSignDetailVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @Author: Xintao Hu
 * @Desctription: TODO
 * @Date: Modified on 2026/1/8 15:37
 * @Version: 1.0
 */
@RestController
@RequestMapping("/mini/activities")
public class MiniActivityController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private RegistrationService registrationService;

    //查询活动列表
    @GetMapping
    public AjaxResult<PageResult<ActivityDetailVo>> listDetail(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                               @RequestParam(required = false) String activityName,
                                                               @RequestParam(required = false) Integer status) {
        return AjaxResult.success(activityService.listDetail(pageNum, pageSize, activityName, status));
    }

    //报名
    @PostMapping("/{activityId}/registrations")
    public AjaxResult<Boolean> userJoin(@PathVariable Long activityId,
                                        @RequestParam Long userId) {

        return AjaxResult.success(registrationService.userJoin(userId, activityId));
    }


    //查询打卡日历
    @GetMapping("/{activityId}/sign-days")
    public AjaxResult<UserSignDaysVo> getSignDays(@PathVariable Long activityId,
                                                  @RequestParam Long userId) {

        return AjaxResult.success(registrationService.getSignDays(userId, activityId));
    }

    //打卡
    //活动id，任务id，userId,

    @PostMapping("/{activityId}/signs")
    public AjaxResult<UserSignDetailVo> sign(@PathVariable Long activityId,
                                             @RequestParam Long userId,
                                             @RequestParam Long taskId) {

        return AjaxResult.success(registrationService.sign(userId, activityId, taskId));
    }


}
