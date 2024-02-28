package com.yupi.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @Author: 艾伦
 */
@SpringBootTest
class RedissonLimitManagerTest {
    @Resource
    private RedissonLimitManager RedissonLimitManager;

    @Test
    void doRateLimit() {
        String key = "user_limit";
        for (int i = 0; i < 5; i++) {
            RedissonLimitManager.doRateLimit(key);
            System.out.println("成功");
        }
    }
}