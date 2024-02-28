package com.yupi.springbootinit.job.cycle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.BiMqConstant;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.constant.GenStatusConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.yupi.springbootinit.model.enums.ChartStatusEnum.SUCCEED;

/**
 * Description:
 *
 * @Author: 艾伦
 */
// 取消注解开启任务
//@Component
public class ChartStatusJob {
    @Resource
    private ChartService chartService;
    @Resource
    private AiManager aiManager;

    @Scheduled
    public void run() {
        // todo 给未成功生成的图表添加定时任务
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chartStatus", GenStatusConstant.FAILED);
        List<Chart> failedCharts = chartService.list(queryWrapper);
        for (Chart failedChart : failedCharts) {
            String userInput = chartService.buildUserInput(failedChart);
            String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, userInput);
            // 进行数据分割
            String[] split = result.split("【【【【【");
            if (split.length < 3) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
            }
            String genChart = split[1].trim();
            String genResult = split[2].trim();
            failedChart.setGenChart(genChart);
            failedChart.setGenResult(genResult);
            failedChart.setChartStatus(SUCCEED.getValue());
            boolean updateResult = chartService.updateById(failedChart);
            if (!updateResult) {
                chartService.handleChartUpdateError(failedChart.getId(), "图表状态更改失败");
            }
        }
    }
}
