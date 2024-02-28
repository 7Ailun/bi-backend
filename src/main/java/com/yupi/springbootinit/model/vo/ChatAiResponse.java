package com.yupi.springbootinit.model.vo;

import lombok.Data;

/**
 * Description:
 *
 * @Author: 艾伦
 */
@Data
public class ChatAiResponse {

    /**
     * 问题
     */
    private String question;

    /**
     * 问题名称
     */
    private String name;

    /**
     * 生成结果
     */
    private String genResult;

    /**
     * 问题类型
     */
    private String chatType;

}
