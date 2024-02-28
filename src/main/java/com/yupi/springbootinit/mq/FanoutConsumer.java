package com.yupi.springbootinit.mq;

import com.rabbitmq.client.*;

public class FanoutConsumer {

  private static final String CHART_EXCHANGE_NAME = "fanout_exchange";
  private static final String QueueName1 = "xiaowang_queue";
  private static final String QueueName2 = "xiaoli_queue";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel1 = connection.createChannel();
    Channel channel2 = connection.createChannel();

    channel1.exchangeDeclare(CHART_EXCHANGE_NAME, "fanout");
    channel2.exchangeDeclare(CHART_EXCHANGE_NAME, "fanout");
    channel1.queueDeclare(QueueName1,true,false,false,null);
    channel2.queueDeclare(QueueName2,true,false,false,null);

        channel1.queueBind(QueueName1, CHART_EXCHANGE_NAME,"" );
        channel2.queueBind(QueueName2, CHART_EXCHANGE_NAME,"" );
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received '" +
            delivery.getEnvelope().getRoutingKey() + "':'" + delivery.getEnvelope().getDeliveryTag() + "':'" + message + "'");
    };

    channel1.basicConsume(QueueName1, true, deliverCallback, consumerTag -> { });
    channel1.basicConsume(QueueName2, true, deliverCallback, consumerTag -> { });
  }
}