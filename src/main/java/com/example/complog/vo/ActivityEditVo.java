package com.example.complog.vo;

import com.example.complog.domain.Activity;
import lombok.Data;

import java.util.List;

/**
 * @Desctription: TODO
 * @Date: Modified on 2026/1/8 15:37
 * @Version: 1.0
 */
@Data
public class ActivityEditVo extends Activity {


    /**
     * 任务列表
     */
    private List<Long> taskIdList;
}
