package com.cc.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.api.dto.ItemDTO;
import com.cc.api.dto.OrderDetailDTO;
import com.cc.common.domain.PageDTO;
import com.cc.common.exception.BizIllegalException;
import com.cc.common.utils.BeanUtils;
import com.cc.item.domain.po.Item;
import com.cc.item.domain.po.ItemDoc;
import com.cc.item.domain.query.ItemPageQuery;
import com.cc.item.mapper.ItemMapper;
import com.cc.item.service.IItemService;
import com.cc.item.service.ISearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

    private final ISearchService searchService;
    @Override
    @Transactional
    public void deductStock(List<OrderDetailDTO> items) {
        String sqlStatement = "com.hmall.item.mapper.ItemMapper.updateStock";
        boolean r = false;
        try {
            r = executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        } catch (Exception e) {
            throw new BizIllegalException("更新库存异常，可能是库存不足", e);
        }
        if (!r) {
            throw new BizIllegalException("库存不足！");
        }
    }

    @Override
    public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
        return BeanUtils.copyList(listByIds(ids), ItemDTO.class);
    }

    @Override
    public PageDTO<ItemDoc> Search(ItemPageQuery query) throws IOException {
        if (query == null) {
            return PageDTO.empty(0L, 0L);
        }
        PageDTO<ItemDoc> result = searchService.RedisSearch(query);
        if (result == null) {
            result = searchService.EsSearch(query);
        }
        return result;
    }


}
