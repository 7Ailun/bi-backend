package com.yupi.springbootinit.bizmq;

import com.yupi.springbootinit.constant.BiMqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;

/**
 * Description:
 *
 * @Author: 艾伦
 */
@Component
public class BiMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息（chart）
     * @param message
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.CHART_EXCHANGE_NAME,BiMqConstant.CHART_ROUTING_KEY,message);
    }

    /**
     * 发送死信消息（chart）
     * @param message
     */
    public void sendDeadLetterMessage(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.CHART_EXCHANGE_NAME,BiMqConstant.CHART_DEAD_LETTER_ROUTING_KEY,message);
    }


}
