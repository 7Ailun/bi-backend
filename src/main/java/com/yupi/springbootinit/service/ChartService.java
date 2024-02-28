package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.vo.BiResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 艾伦
 * @description 针对表【chart(图表信息表)】的数据库操作Service
 * @createDate 2023-11-15 10:45:50
 */
public interface ChartService extends IService<Chart> {

    void handleChartUpdateError(long chartId, String execMessage);

    String buildUserInput(Chart chart);

    BiResponse regenChartByAiAsyncMq(long chartId);
}
