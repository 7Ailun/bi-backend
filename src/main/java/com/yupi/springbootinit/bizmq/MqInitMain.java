package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * Description: 用于创建测试程序用到的交换机 和 队列（只在程序启动前执行一次）
 *
 * @Author: 艾伦
 */
public class MqInitMain {
    public static void main(String[] args) {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            String CHART_EXCHANGE_NAME = "code_exchange";
            channel.exchangeDeclare(CHART_EXCHANGE_NAME, "direct");
            String CHART_QUEUE_NAME = "code_queue";
            channel.queueDeclare(CHART_QUEUE_NAME,true,false,false,null);
            channel.queueBind(CHART_QUEUE_NAME,CHART_EXCHANGE_NAME,"my_routingKey");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
