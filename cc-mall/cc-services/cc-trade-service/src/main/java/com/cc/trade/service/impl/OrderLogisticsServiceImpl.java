package com.cc.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.trade.domain.po.OrderLogistics;
import com.cc.trade.mapper.OrderLogisticsMapper;
import com.cc.trade.service.IOrderLogisticsService;
import org.springframework.stereotype.Service;


@Service
public class OrderLogisticsServiceImpl extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements IOrderLogisticsService {
}
