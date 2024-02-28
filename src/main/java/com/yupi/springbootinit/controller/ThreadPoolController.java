package com.yupi.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description:
 *
 * @Author: 艾伦
 */
@RestController
@RequestMapping("/pool")
@Profile({"dev","local"})
public class ThreadPoolController {
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @GetMapping("/add")
    public void add(String name) {
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("任务执行中：" + name + "执行人:" + Thread.currentThread().getName());
                Thread.sleep(10000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },threadPoolExecutor);
    }

    @GetMapping("/get")
    public String get() {
        Map<String,Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        map.put("队列长度",size);
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        map.put("最大线程数",maximumPoolSize);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已完成任务数",completedTaskCount);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("总任务数",taskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("活跃线程数",activeCount);
        String str = JSONUtil.toJsonStr(map);
        return str;
    }

}
