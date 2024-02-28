package com.yupi.springbootinit.model.enums;

/**
 * Description:
 *
 * @Author: 艾伦
 */
public enum ChartStatusEnum {

    WAIT("等待生成","wait"),
    RUNNING("正在生成", "running"),
    SUCCEED("生成成功", "succeed"),
    FAILED("生成失败", "failed");

    private String status;
    private String value;

    ChartStatusEnum(String status, String value) {
        this.status = status;
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public String getStatus() {
        return status;
    }


}
