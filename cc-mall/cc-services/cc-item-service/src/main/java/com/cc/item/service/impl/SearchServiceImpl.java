package com.cc.item.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.api.dto.ItemDTO;
import com.cc.common.domain.PageDTO;
import com.cc.common.utils.BeanUtils;
import com.cc.common.utils.CollUtils;
import com.cc.item.domain.po.Item;
import com.cc.item.domain.query.ItemPageQuery;
import com.cc.item.mapper.SearchMapper;
import com.cc.item.service.ISearchService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl extends ServiceImpl<SearchMapper, Item> implements ISearchService {

    private final StringRedisTemplate stringRedisTemplate;

    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.245.128:9200")
    ));
    @Override
    public PageDTO<ItemDTO> EsSearch(ItemPageQuery query) throws IOException {

        PageDTO<ItemDTO> result = new PageDTO<>();


        //1. 准备Request
        SearchRequest request = new SearchRequest("items");

        //2. 构造查询条件
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();

        request.source().trackTotalHits(true);

        //3. 复合查询
        if (query.getKey() != null && ! query.getKey().equals("")) {
            boolQuery.must(QueryBuilders.matchQuery("name", query.getKey()));
        }


        if (query.getSortBy() != null && !"".equals(query.getSortBy())) {
            request.source().sort(query.getSortBy(), query.getIsAsc() ? SortOrder.ASC : SortOrder.DESC);
        } else {
            request.source().sort("updateTime", query.getIsAsc() ? SortOrder.ASC : SortOrder.DESC);
        }


        if (query.getCategory() != null && ! query.getCategory().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("category",query.getCategory()));
        }

        if (query.getBrand() != null && ! query.getBrand().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("brand",query.getBrand()));
        }

        if (query.getMinPrice() != null && query.getMaxPrice() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(query.getMinPrice()).lte(query.getMaxPrice()));
        }

        request.source().query(boolQuery);

        request.source().highlighter(
                SearchSourceBuilder.highlight()
                        .field("name")
                        .preTags("<em>")
                        .postTags("</em>")
        );

        request.source().from(query.from()).size(query.getPageSize());

        //4. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //5. 解析结果
        result.setTotal(response.getHits().getTotalHits().value);
        result.setPages(result.getTotal() % query.getPageSize() == 0 ?
                result.getTotal() / query.getPageSize() :
                (result.getTotal() / query.getPageSize()) + 1);

        final SearchHit[] hits = response.getHits().getHits();
        List<ItemDTO> list = new ArrayList<>();

        for (SearchHit hit : hits) {
            ItemDTO itemDTO = JSONUtil.toBean(hit.getSourceAsString(), ItemDTO.class);
            Map<String, HighlightField> hfs = hit.getHighlightFields();
            if (CollUtils.isNotEmpty(hfs)) {
                HighlightField hf = hfs.get("name");
                if (hf != null) {
                    itemDTO.setName(hf.getFragments()[0].toString());
                }
            }
            list.add(itemDTO);
        }
        result.setList(list);
        return result;
    }



    @Override
    public PageDTO<ItemDTO> RedisSearchId(ItemPageQuery query) throws IOException {
        PageDTO<ItemDTO> result = new PageDTO<>();
        Set<String> productIds = null;
        if (query.getKey() != null && !query.getKey().isEmpty()) {
            String key = "search:" + query.getKey().replaceAll("\\s+", "");
            productIds = stringRedisTemplate.opsForSet().members(key);
        }
        if (query.getCategory() != null && !query.getCategory().isEmpty()) {
            String categoryKey = "category:" + query.getCategory().replaceAll("\\s+", "");
            productIds = stringRedisTemplate.opsForSet().members(categoryKey);
        }
        if (query.getBrand() != null && !query.getBrand().isEmpty()) {
            String brandKey = "brand:" + query.getBrand().replaceAll("\\s+", "");
            productIds = stringRedisTemplate.opsForSet().members(brandKey);
        }

        if (productIds != null && !productIds.isEmpty()) {
            result = RedisSearchInfo(productIds); // 假设有一个方法通过商品ID查询商品详情
        }
        return result;
    }

    @Override
    public PageDTO<ItemDTO> RedisSearchInfo(Set<String> IdSet) {
        // 结果列表
        List<ItemDTO> items = new ArrayList<>();

        // 遍历商品ID集合，逐个查询商品详情
        for (String productId : IdSet) {
            // 假设 Redis 存储商品的 Hash 键为 "product:<商品ID>"
            String productKey = "product:" + productId;

            // 获取商品详细信息，返回的是一个 Map<Object, Object>，其中包含商品的所有字段和对应的值
            Map<Object, Object> productDetails = stringRedisTemplate.opsForHash().entries(productKey);

            // 如果商品信息存在，则将其转换为 ItemDTO 对象
            if (productDetails != null && !productDetails.isEmpty()) {
                ItemDTO itemDTO = BeanUtils.copyBean(productDetails, ItemDTO.class);
                items.add(itemDTO);
            }
        }

        // 获取总记录数和总页数，假设总记录数为 items.size()
        Long total = (long) items.size();  // 这里你可能还需要根据实际情况设置总记录数
        Long pages = total / 10 + (total % 10 == 0 ? 0 : 1);  // 假设每页显示10条数据，计算总页数

        // 返回分页数据
        return new PageDTO<>(total, pages, items);  // PageDTO构造函数的正确参数：total, pages, list
    }

}
