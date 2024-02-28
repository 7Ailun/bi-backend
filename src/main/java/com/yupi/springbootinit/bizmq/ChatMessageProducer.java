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
public class ChatMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 发送消息（chat）To Allen
     * @param message
     */
    public void sendMessageToChatAllen(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.CHAT_EXCHANGE_NAME,BiMqConstant.CHAT_ROUTING_KEY,message);
    }

    /**
     * 发送消息（chat）To SanLi
     * @param message
     */
    public void sendMessageToChatSanLi(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.CHAT_EXCHANGE_NAME,BiMqConstant.CHAT_ROUTING_KEY,message);
    }

    /**
     * 发送死信消息（chat）
     * @param message
     */
    public void sendDeadLetterMessageToChat(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.CHAT_EXCHANGE_NAME,BiMqConstant.CHAT_DEAD_LETTER_ROUTING_KEY,message);
    }
}
