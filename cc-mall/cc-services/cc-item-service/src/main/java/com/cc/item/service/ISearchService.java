package com.cc.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.api.dto.ItemDTO;
import com.cc.common.domain.PageDTO;
import com.cc.item.domain.po.Item;
import com.cc.item.domain.po.ItemDoc;
import com.cc.item.domain.query.ItemPageQuery;

import java.io.IOException;
import java.util.Set;

public interface ISearchService extends IService<Item> {

    PageDTO<ItemDTO> EsSearch(ItemPageQuery query) throws IOException;

    PageDTO<ItemDTO> RedisSearchId(ItemPageQuery query) throws IOException;

    PageDTO<ItemDTO> RedisSearchInfo(Set<String> IdSet);
}
