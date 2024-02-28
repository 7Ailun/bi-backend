package com.yupi.springbootinit.model.dto.chat;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class ChatQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * id
     */
    private Long userId;

    /**
     * 问题名称
     */
    private String name;
    /**
     * 问题
     */
    private String question;


    /**
     * 问题类型
     */
    private String chatType;
    /**
     * 生成结果
     */
    private String genResult;

    /**
     * 当前页
     */
    private long current;
    /**
     * 页面大小
     */
    private long pageSize;



    private static final long serialVersionUID = 1L;
}