package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.bizmq.ChatMessageProducer;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.constant.GenStatusConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.dto.chat.ChatQueryRequest;
import com.yupi.springbootinit.model.dto.chat.GenChatByAiRequest;

import com.yupi.springbootinit.model.entity.Chat;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.ChatAiResponse;
import com.yupi.springbootinit.service.ChatService;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 *
 * @Author: 艾伦
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatAiController {

    @Resource
    private AiManager aiManager;
    @Resource
    private ChatService chatService;

    @Resource
    private UserService userService;

    @Resource
    private ChatMessageProducer chatMessageProducer;


    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chat>> getMyChatListByPage(@RequestBody ChatQueryRequest chatQueryRequest,
                                                HttpServletRequest request) {
        if (chatQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        chatQueryRequest.setUserId(loginUser.getId());
        // 获取分页参数
        long current = chatQueryRequest.getCurrent();
        long size = chatQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chat> chatPage = chatService.page(new Page<>(current, size),
                getQueryWrapper(chatQueryRequest));
        return ResultUtils.success(chatPage);
    }



    /**
     * 生成回答
     *
     * @param genChatByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/result")
    public BaseResponse<ChatAiResponse> genResultByAi(GenChatByAiRequest genChatByAiRequest, HttpServletRequest request) {
        // 参数校验
        if (genChatByAiRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String question = genChatByAiRequest.getQuestion();
        if (StringUtils.isBlank(question)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "问题不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        Chat chat = new Chat();
        // 调用 ai 生成结果
        chat.setChatStatus(GenStatusConstant.RUNNING);
        String genResult = aiManager.doChat(CommonConstant.SANLI_MODEL_ID, question);
        String[] splitResult = genResult.split("【【【【【");
        if (splitResult.length > 4) {
            chat.setGenResult(GenStatusConstant.FAILED);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成错误");
        }
        chat.setChatStatus(GenStatusConstant.SUCCEED);
        String name = splitResult[1];
        String chatType = splitResult[2];
        String chatResult = splitResult[3];

        Long userId = loginUser.getId();
        chat.setName(name);
        chat.setUserId(userId);
        chat.setQuestion(question);
        chat.setChatType(chatType);
        chat.setGenResult(chatResult);
        // 保存数据
        boolean res = chatService.save(chat);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存失败");
        }
        ChatAiResponse chatAiResponse = new ChatAiResponse();
        BeanUtils.copyProperties(chat, chatAiResponse);
        return ResultUtils.success(chatAiResponse);
    }

    /**
     * 生成回答（消息队列）
     *
     * @param genChatByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/result/mq")
    public BaseResponse<ChatAiResponse> genResultByMq(GenChatByAiRequest genChatByAiRequest, HttpServletRequest request) {

        // 参数校验
        if (genChatByAiRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String question = genChatByAiRequest.getQuestion();
        if (StringUtils.isBlank(question)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "问题不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        Chat chat = new Chat();
        Long userId = loginUser.getId();
        chat.setUserId(userId);
        chat.setQuestion(question);
        // 保存数据
        boolean res = chatService.save(chat);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存失败");
        }
        Long chatId = chat.getId();
        // 生产者发送消息
        chatMessageProducer.sendMessageToChatSanLi(String.valueOf(chatId));
        ChatAiResponse chatAiResponse = new ChatAiResponse();
        BeanUtils.copyProperties(chat, chatAiResponse);
        return ResultUtils.success(chatAiResponse);
    }


    private QueryWrapper<Chat> getQueryWrapper(ChatQueryRequest chatQueryRequest) {
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        if (chatQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chatQueryRequest.getId();
        Long userId = chatQueryRequest.getUserId();
        String chatType = chatQueryRequest.getChatType();
        String name = chatQueryRequest.getName();
        String question = chatQueryRequest.getQuestion();
        String genResult = chatQueryRequest.getGenResult();
        String sortField = chatQueryRequest.getSortField();
        String sortOrder = chatQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(chatType), "chatType", chatType);
        queryWrapper.like(StringUtils.isNotBlank(question), "question", question);
        queryWrapper.like(StringUtils.isNotBlank(genResult), "chartType", genResult);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;

    }
}
