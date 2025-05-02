package com.cc.item.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.api.dto.ItemDTO;
import com.cc.common.domain.PageDTO;
import com.cc.common.utils.CollUtils;
import com.cc.item.domain.po.Item;
import com.cc.item.domain.po.ItemDoc;
import com.cc.item.domain.query.ItemPageQuery;
import com.cc.item.mapper.SearchMapper;
import com.cc.item.service.ISearchService;
import com.cc.item.utils.RedisUtil;
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
    public PageDTO<ItemDTO> RedisSearch(ItemPageQuery query) throws IOException {
        return null;
    }
}
