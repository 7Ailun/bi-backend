package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @Author: 艾伦
 */
@Service
public class AiManager {

    @Resource
    private YuCongMingClient client;
    public String doChat(long modelId, String message) {

        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId); // 模型id
        devChatRequest.setMessage(message);

        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        if(response == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"生成结果错误");
        }
        return response.getData().getContent();
    }
}
