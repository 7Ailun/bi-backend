package com.yupi.springbootinit.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


/**
 * Description:
 *
 * @Author: 艾伦
 */
@SpringBootTest
class MyMessageProducerTest {
    @Resource
    private MyMessageProducer myMessageProducer;
    @Test
    void test() {
        myMessageProducer.sendMessage("code_exchange", "my_routingKey", "hello world");
    }

}