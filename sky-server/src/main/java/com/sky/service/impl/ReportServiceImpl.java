package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);//开始时间

        while (begin.equals(end)){//如果begin不等于end就把数值往后遍历
            //日期计算,计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList){
            //查询date日期对应的营业额数据,营业额是指:状态是"已完成"的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);



            //select sum(amount) from orders where order_time > beginTime and order_time < endTime and status = 5
            //5代表已完成
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
           Double turnover = orderMapper.sumByMap(map);
           turnover = turnover == null ? 0.0 : turnover;//判断一天营业额是否为空?是空的话得设置成0.0
           turnoverList.add(turnover);
        }
        //封装返回结构
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))//把list的每个集合里的元素取出来通过","拼接
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }
}
