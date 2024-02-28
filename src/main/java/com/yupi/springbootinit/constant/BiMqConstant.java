package com.yupi.springbootinit.constant;

/**
 * Description:
 *
 * @Author: 艾伦
 */
public interface BiMqConstant {
    /**
     * 图表 交换机
     */
    String CHART_EXCHANGE_NAME = "bi_exchange";
    /**
     * 图表 死信交换机
     */
    String DEAD_LETTER_CHART_EXCHANGE_NAME = "bi_dead_exchange";
    /**
     * 图表队列
     */
    String CHART_QUEUE_NAME = "bi_queue";
    /**
     * 图表 死信队列
     */
    String DEAD_LETTER_CHART_QUEUE_NAME = "bi_dead_queue";
    /**
     * 图表 路由键
     */
    String CHART_ROUTING_KEY = "bi";
    /**
     * 图表 死信路由键
     */
    String CHART_DEAD_LETTER_ROUTING_KEY = "failed";
    /**
     * 聊天 交换机
     */
    String CHAT_EXCHANGE_NAME = "chat_exchange";
    /**
     * 聊天 队列
     */
    String CHAT_QUEUE_NAME = "chat_queue";
    /**
     * 聊天 死信交换机
     */
    String DEAD_LETTER_CHAT_EXCHANGE_NAME = "chat_dead_exchange";
    /**
     * 聊天 死信队列
     */

    String DEAD_LETTER_CHAT_QUEUE_NAME = "chat_dead_queue";

    /**
     * 图表 路由键
     */
    String CHAT_ROUTING_KEY = "chat";

    /**
     * 聊天 死信路由键
     */
    String CHAT_DEAD_LETTER_ROUTING_KEY = "chat_dead";
    
}
