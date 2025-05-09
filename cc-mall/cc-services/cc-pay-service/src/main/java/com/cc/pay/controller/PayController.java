package com.cc.pay.controller;

import com.cc.api.dto.PayOrderDTO;
import com.cc.common.exception.BizIllegalException;
import com.cc.common.utils.BeanUtils;
import com.cc.pay.domain.dto.PayApplyDTO;
import com.cc.pay.domain.dto.PayOrderFormDTO;
import com.cc.pay.domain.po.PayOrder;
import com.cc.pay.enums.PayType;
import com.cc.pay.service.IPayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "支付相关接口")
@RestController
@RequestMapping("pay-orders")
@RequiredArgsConstructor
public class PayController {

    private final IPayOrderService payOrderService;

    @ApiOperation("生成支付单")
    @PostMapping
    public String applyPayOrder(@RequestBody PayApplyDTO applyDTO){
        if(!PayType.BALANCE.equalsValue(applyDTO.getPayType())){
            // 目前只支持余额支付
            throw new BizIllegalException("抱歉，目前只支持余额支付");
        }
        return payOrderService.applyPayOrder(applyDTO);
    }

    @ApiOperation("尝试基于用户余额支付")
    @ApiImplicitParam(value = "支付单id", name = "id")
    @PostMapping("{id}")
    public void tryPayOrderByBalance(@PathVariable("id") Long id, @RequestBody PayOrderFormDTO payOrderFormDTO){
        payOrderFormDTO.setId(id);
        payOrderService.tryPayOrderByBalance(payOrderFormDTO);
    }
    @ApiOperation("根据id查询支付单")
    @GetMapping("/biz/{id}")
    public PayOrderDTO queryPayOrderByBizOrderNo(@PathVariable("id") Long id){
        PayOrder payOrder = payOrderService.lambdaQuery().eq(PayOrder::getBizOrderNo, id).one();
        return BeanUtils.copyBean(payOrder, PayOrderDTO.class);
    }

    /**
     * 修改支付单状态
     *
     * @param orderId
     */
    @ApiOperation("修改支付单状态")
    @PutMapping("/status/{orderId}/{status}")
    void updatePayOrderStatusByOrderId(@PathVariable Long orderId, @PathVariable Integer status) {
        payOrderService.lambdaUpdate()
                .set(PayOrder::getStatus, status)
                .eq(PayOrder::getBizOrderNo, orderId)
                .update();
    }

    /*// 方便接口测试
    @ApiOperation("查询支付单")
    @GetMapping
    public List<PayOrderVO> queryPayOrders(){
        return BeanUtils.copyList(payOrderService.list(), PayOrderVO.class);
    }*/
}