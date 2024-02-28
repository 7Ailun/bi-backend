package com.yupi.springbootinit.mapper;

import cn.hutool.core.util.ArrayUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.lang.reflect.Array;

/**
 * Description:
 *
 * @Author: 艾伦
 */
@SpringBootTest
public class ChartMapperTest {
    @Resource
    private ChartMapper chartMapper;

    @Test
    void test() {
        String chartId = "1726536032086757378";
        String sql = String.format("select * from chart_%s", chartId);
        chartMapper.getChartData(sql);
    }

    @Test
    void createChartTest() {
        String csvData =
                "日期,用户数\n" +
                        "1号,10\n" +
                        "2号,20\n" +
                        "3号,30";
        // 拿到多少行
        String[] row = csvData.split("\n");
        // 拿到多少列
        String[] col = row[0].split(",");
        String[][] data = new String[row.length][col.length];
        for (int i = 0; i < row.length; i++) {
            String[] split = row[i].split(",");
            for (int j = 0; j < col.length; j++) {
                if (i == 0) {
                    data[i][j] = split[j];
                }
                data[i][j] = split[j];
            }
        }
        String sql = String.format("create table chart_1724625108463640577\n" +
                "(\n" +
                "    月份   int null,\n" +
                "    用户数 int null\n" +
                ");");

        System.out.println(ArrayUtil.toString(data));
    }
}
