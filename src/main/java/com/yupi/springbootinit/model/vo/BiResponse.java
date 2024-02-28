package com.yupi.springbootinit.model.vo;

import lombok.Data;

/**
 * Description:
 *
 * @Author: 艾伦
 */
@Data
public class BiResponse {
    /**
     * Echarts图表代码
     */
    private String genChart;
    /**
     * 分析结果
     */
    private String genResult;

    /**
     * 图表Id
     */
    private long chartId;
}
