package com.cc.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cc.api.dto.OrderDetailDTO;
import com.cc.item.domain.po.Item;
import org.apache.ibatis.annotations.Update;

public interface ItemMapper extends BaseMapper<Item> {

    @Update("UPDATE item SET stock = stock - #{num} WHERE id = #{itemId}")
    void updateStock(OrderDetailDTO orderDetail);
}
