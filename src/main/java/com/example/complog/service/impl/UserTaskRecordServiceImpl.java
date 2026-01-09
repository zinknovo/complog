package com.example.complog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.complog.domain.UserTaskRecord;
import com.example.complog.service.UserTaskRecordService;
import com.example.complog.mapper.UserTaskRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author Z1nk
* @description 针对表【user_task_record】的数据库操作Service实现
* @createDate 2026-01-08 16:10:57
*/
@Service
public class UserTaskRecordServiceImpl extends ServiceImpl<UserTaskRecordMapper, UserTaskRecord>
    implements UserTaskRecordService{

}




