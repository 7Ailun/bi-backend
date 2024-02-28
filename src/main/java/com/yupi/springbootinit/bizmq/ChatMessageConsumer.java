package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.BiMqConstant;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.constant.GenStatusConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chat;
import com.yupi.springbootinit.service.ChatService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @Author: 艾伦
 */
@Component
public class ChatMessageConsumer {
    @Resource
    private ChatService chatService;
    @Resource
    private AiManager aiManager;
    @Resource
    private ChatMessageProducer chatMessageProducer;

    // 指定程序监听的消息队列和确认机制
    // 此消费者 不会生成 问题名称（name）如需使用 取消注解
//    @RabbitListener(queues = BiMqConstant.CHAT_QUEUE_NAME, ackMode = "MANUAL")
    @SneakyThrows
    public void receiveMessageByAllen(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if(StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"消息为空");
        }
        // 获取 图表信息
        long chatId = Long.parseLong(message);
        Chat chat = chatService.getById(chatId);
        if (chat == null) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"问题不存在");
        }
        // 状态：运行
        chat.setChatStatus(GenStatusConstant.RUNNING);
        String question = chat.getQuestion();
        // 调用 AI
        String genResult = aiManager.doChat(CommonConstant.ALLEN_MODEL_ID, question);
        String[] splitResult = genResult.split("【【【【【");
        if (splitResult.length > 3) {
            chat.setGenResult(GenStatusConstant.FAILED);
            channel.basicNack(deliveryTag,false,false);
            // 发送死信消息
            chatMessageProducer.sendDeadLetterMessageToChat(String.valueOf(chatId));
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成错误");
        }
        chat.setChatStatus(GenStatusConstant.SUCCEED);
        String chatType = splitResult[1];
        String chatResult = splitResult[2];
        chat.setGenResult(chatResult);
        chat.setChatType(chatType);
        boolean update = chatService.updateById(chat);
        if(!update) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据更新异常");
        }
        channel.basicAck(deliveryTag,false);
    }
    @RabbitListener(queues = BiMqConstant.CHAT_QUEUE_NAME, ackMode = "MANUAL")
    @SneakyThrows
    public void receiveMessageBySanLi(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if(StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"消息为空");
        }
        // 获取 图表信息
        long chatId = Long.parseLong(message);
        Chat chat = chatService.getById(chatId);
        if (chat == null) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"问题不存在");
        }
        // 状态：运行
        chat.setChatStatus(GenStatusConstant.RUNNING);
        String question = chat.getQuestion();
        // 调用 AI
        String genResult = aiManager.doChat(CommonConstant.SANLI_MODEL_ID, question);
        String[] splitResult = genResult.split("【【【【【");
        if (splitResult.length > 4) {
            chat.setGenResult(GenStatusConstant.FAILED);
            channel.basicNack(deliveryTag,false,false);
            // 发送死信消息
            chatMessageProducer.sendDeadLetterMessageToChat(String.valueOf(chatId));
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成错误");
        }
        chat.setChatStatus(GenStatusConstant.SUCCEED);
        String name = splitResult[1];
        String chatType = splitResult[2];
        String chatResult = splitResult[3];
        chat.setName(name);
        chat.setGenResult(chatResult);
        chat.setChatType(chatType);
        boolean update = chatService.updateById(chat);
        if(!update) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据更新异常");
        }
        channel.basicAck(deliveryTag,false);
    }

    /**
     * 监听死信队列，消费失败信息
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @RabbitListener(queues = BiMqConstant.DEAD_LETTER_CHAT_QUEUE_NAME, ackMode = "MANUAL")
    @SneakyThrows
    public void receiveDeadLetterMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        long chatId = Long.parseLong(message);
        Chat chat = chatService.getById(chatId);
        chat.setChatStatus(GenStatusConstant.FAILED);
        boolean result = chatService.updateById(chat);
        if(!result) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据更新异常");
        }
    }

}
