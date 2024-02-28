package com.yupi.springbootinit.bizmq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.constant.GenStatusConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.constant.BiMqConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yupi.springbootinit.model.enums.ChartStatusEnum.*;


/**
 * Description:
 *
 * @Author: 艾伦
 */
@Component
@Slf4j
public class BiMessageConsumer {
    @Resource
    private ChartService chartService;
    @Resource
    private AiManager aiManager;
    @Resource
    private BiMessageProducer biMessageProducer;

    // 指定程序监听的消息队列和确认机制
    @RabbitListener(queues = BiMqConstant.CHART_QUEUE_NAME, ackMode = "MANUAL")
    @SneakyThrows
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if(StringUtils.isBlank(message)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表不存在");
        }
        // todo 接收到消息进行的处理逻辑
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setChartStatus(RUNNING.getValue());
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            channel.basicNack(deliveryTag, false, false);
            chartService.handleChartUpdateError(chart.getId(), "图表状态更改失败");
            // 更新失败，发送死信消息
            biMessageProducer.sendDeadLetterMessage(chart.getId().toString());
        }
        // 调用 AI
        String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, buildUserInput(chart).toString());
        // 进行数据分割
        String[] split = result.split("【【【【【");
        if (split.length < 3) {
            channel.basicNack(deliveryTag, false, false);
            biMessageProducer.sendDeadLetterMessage(chart.getId().toString());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        String genChart = split[1].trim();
        String genResult = split[2].trim();
        updateChart.setGenChart(genChart);
        updateChart.setGenResult(genResult);
        updateChart.setChartStatus(SUCCEED.getValue());
        boolean updateResult = chartService.updateById(updateChart);
        if (!updateResult) {
            channel.basicNack(deliveryTag, false, false);
            chartService.handleChartUpdateError(chart.getId(), "图表状态更改失败");
        }
        channel.basicAck(deliveryTag, false);
    }

    /**
     * 监听死信队列，消费失败信息
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @RabbitListener(queues = BiMqConstant.DEAD_LETTER_CHART_QUEUE_NAME, ackMode = "MANUAL")
    @SneakyThrows
    public void receiveDeadLetterMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if(StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
        // 根据图表 id 更新图表状态为 failed
        long chartId = Long.parseLong(message);
        Chart chart = new Chart();
        chart.setChartStatus(GenStatusConstant.FAILED);
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", chartId);
        boolean result = chartService.update(chart, queryWrapper);
        if(!result) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据更新失败");
        }
        channel.basicAck(deliveryTag,false);
    }
    /**
     * 构造用户输入
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart) {
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


}
