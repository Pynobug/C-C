package com.cc.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.common.domain.PageDTO;
import com.cc.item.domain.po.Item;
import com.cc.item.domain.po.ItemDoc;
import com.cc.item.domain.query.ItemPageQuery;

import java.io.IOException;

public interface ISearchService extends IService<Item> {

    PageDTO<ItemDoc> EsSearch(ItemPageQuery query) throws IOException;

    PageDTO<ItemDoc> RedisSearch(ItemPageQuery query) throws IOException;
}
