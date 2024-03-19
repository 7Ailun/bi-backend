package com.wei.springbootinit.manager;

import com.yupi.springbootinit.manager.AiManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @Author: 艾伦
 */
@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;
    private long modelId = 1725719690787332097L;
    @Test
    void doChat() {
        String result = aiManager.doChat(modelId,"分析需求：\n" +
                "分析网站用户的增长情况\n" +
                "原始数据：\n" +
                "日期,用户数\n" +
                "1号,10\n" +
                "2号,20\n" +
                "3号,30");
        System.out.println( result);
    }
}