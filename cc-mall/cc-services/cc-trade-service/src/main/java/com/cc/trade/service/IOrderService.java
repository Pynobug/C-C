package com.cc.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.trade.domain.dto.OrderFormDTO;
import com.cc.trade.domain.po.Order;

public interface IOrderService extends IService<Order> {
    Long createOrder(OrderFormDTO orderFormDTO);

    void markOrderPaySuccess(Long orderId);

    void cancelOrder(Long orderId);
}
