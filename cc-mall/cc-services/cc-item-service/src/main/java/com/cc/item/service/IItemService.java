package com.cc.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.api.dto.ItemDTO;
import com.cc.api.dto.OrderDetailDTO;
import com.cc.common.domain.PageDTO;
import com.cc.item.domain.po.Item;
import com.cc.item.domain.po.ItemDoc;
import com.cc.item.domain.query.ItemPageQuery;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 */
public interface IItemService extends IService<Item> {

    void deductStock(List<OrderDetailDTO> items);

    List<ItemDTO> queryItemByIds(Collection<Long> ids);

    PageDTO<ItemDTO> Search(ItemPageQuery query) throws IOException;

    void addItem(Item item);

    void publish(Long itemId);

    void down(Long itemId);

    void delete(Long itemId);
}