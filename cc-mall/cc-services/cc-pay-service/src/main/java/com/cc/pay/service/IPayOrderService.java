package com.cc.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.pay.domain.dto.PayApplyDTO;
import com.cc.pay.domain.dto.PayOrderFormDTO;
import com.cc.pay.domain.po.PayOrder;


public interface IPayOrderService extends IService<PayOrder> {

    String applyPayOrder(PayApplyDTO applyDTO);

    void tryPayOrderByBalance(PayOrderFormDTO payOrderFormDTO);
}