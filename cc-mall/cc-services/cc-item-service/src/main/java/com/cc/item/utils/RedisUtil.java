package com.cc.item.utils;

import com.cc.item.domain.query.ItemPageQuery;

public class RedisUtil {
    public static String buildRedisKey(ItemPageQuery query) {
        return String.format("item:search:%s:%s:%s:%d:%d:%d:%d",
                query.getKey() != null ? query.getKey() : "",
                query.getCategory() != null ? query.getCategory() : "",
                query.getBrand() != null ? query.getBrand() : "",
                query.getMinPrice() != null ? query.getMinPrice() : 0,
                query.getMaxPrice() != null ? query.getMaxPrice() : Integer.MAX_VALUE,
                query.from(),
                query.getPageSize()
        );
    }
}
