package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

import static com.yupi.springbootinit.constant.BiMqConstant.*;


/**
 * Description:
 *
 * @Author: 艾伦
 */
public class BiInitMain {
    public static void main(String[] args) {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            Connection connection = connectionFactory.newConnection();
            // 声明信道
            Channel channel = connection.createChannel();

            // 声明交换机
            channel.exchangeDeclare(CHART_EXCHANGE_NAME, "direct",true);
            channel.exchangeDeclare(DEAD_LETTER_CHART_EXCHANGE_NAME, "direct",true);
            Map<String,Object> arg = new HashMap<>();
            // 死信队列参数
            arg.put("x-dead-letter-exchange", DEAD_LETTER_CHART_EXCHANGE_NAME);
            arg.put("x-dead-letter-routing-key", CHART_DEAD_LETTER_ROUTING_KEY);
            // 声明队列
            channel.queueDeclare(CHART_QUEUE_NAME,true,false,false,arg);
            channel.queueDeclare(DEAD_LETTER_CHART_QUEUE_NAME,true,false,false,null);
            channel.queueBind(CHART_QUEUE_NAME,CHART_EXCHANGE_NAME,CHART_ROUTING_KEY);
            channel.queueBind(DEAD_LETTER_CHART_QUEUE_NAME,CHART_EXCHANGE_NAME,"failed");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
