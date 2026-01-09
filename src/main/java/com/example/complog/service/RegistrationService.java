package com.example.complog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.complog.domain.Registration;
import com.example.complog.vo.UserSignDaysVo;
import com.example.complog.vo.UserSignDetailVo;

/**
* @author Z1nk
* @description 针对表【registration】的数据库操作Service
* @createDate 2026-01-08 16:10:57
*/
public interface RegistrationService extends IService<Registration> {
    boolean userJoin(Long userId, Long activityId);

    UserSignDaysVo getSignDays(Long userId, Long activityId);

    UserSignDetailVo sign(Long userId, Long activityId, Long taskId);

}
