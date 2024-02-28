package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.bizmq.BiMessageProducer;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.constant.GenStatusConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.vo.BiResponse;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.mapper.ChartMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.yupi.springbootinit.model.enums.ChartStatusEnum.FAILED;
import static com.yupi.springbootinit.model.enums.ChartStatusEnum.SUCCEED;

/**
* @author 艾伦
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2023-11-15 10:45:50
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{
    @Resource
    private AiManager aiManager;

    /**
     * 失败回调方法
     * @param chartId
     * @param execMessage
     */
    @Override
    public void handleChartUpdateError(long chartId, String execMessage) {
        Chart chart = new Chart();
        chart.setId(chartId);
        chart.setChartStatus(FAILED.getValue());
        boolean result = updateById(chart);
        if(!result) {
            log.error("更新图标失败状态失败" + chartId + "," + execMessage);
        }
    }

    @Override
    public String buildUserInput(Chart chart) {
        // 构造用户需求
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        String chartGoal = chart.getGoal();
        String chartType = chart.getChartType();
        if (StringUtils.isNotBlank(chartType)){
            chartGoal += ",请使用" + chartType;
        }
        userInput.append(chartGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据
        String csvData = chart.getChartData();
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }

    @Override
    public BiResponse regenChartByAiAsyncMq(long chartId) {

        Chart chart = this.getById(chartId);
        ThrowUtils.throwIf(chart == null,ErrorCode.NOT_FOUND_ERROR,"图表不存在");
        ThrowUtils.throwIf(chart.getChartStatus().equals(SUCCEED.getValue()),ErrorCode.PARAMS_ERROR,"图表已经生成");

        String userInput = buildUserInput(chart);
        if(StringUtils.isBlank(userInput)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户需求不存在");
        }
        chart.setChartStatus(GenStatusConstant.RUNNING);
        boolean b = this.updateById(chart);
        if(!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        // 调用 ai 分析
        String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, userInput);
        String[] split = result.split("【【【【【");
        if(split.length > 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"生成图表失败");
        }
        chart.setChartStatus(GenStatusConstant.SUCCEED);
        String genChart = split[1].trim();
        String genResult = split[2].trim();
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        boolean update = this.updateById(chart);
        if(!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chartId);
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        return biResponse;
    }
}




