package com.cc.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.api.dto.ItemDTO;
import com.cc.api.dto.OrderDetailDTO;
import com.cc.common.domain.PageDTO;
import com.cc.common.exception.BizIllegalException;
import com.cc.common.utils.BeanUtils;
import com.cc.item.domain.po.Item;
import com.cc.item.domain.query.ItemPageQuery;
import com.cc.item.enums.ItemStatus;
import com.cc.item.mapper.ItemMapper;
import com.cc.item.service.IItemService;
import com.cc.item.service.ISearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

    private final ISearchService searchService;

    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.245.128:9200")
    ));

    @Override
    @Transactional
    public void deductStock(List<OrderDetailDTO> items) {
        String sqlStatement = "com.cc.item.mapper.ItemMapper.updateStock";
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
    public PageDTO<ItemDTO> Search(ItemPageQuery query) throws IOException {
        if (query == null) {
            return PageDTO.empty(0L, 0L);
        }
        PageDTO<ItemDTO> result = searchService.RedisSearch(query);
        if (result == null) {
            result = searchService.EsSearch(query);
        }
        return result;
    }

    @Override
    public void addItem(ItemDTO itemDTO) {
        Item item = BeanUtils.copyBean(itemDTO, Item.class);
        item.setStatus(ItemStatus.DOWN); // 默认待审核
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        save(item);
    }

    @Override
    public void publish(Long itemId){
        Item item = getById(itemId);
        if (item != null && item.getStatus() == ItemStatus.DOWN) {
            item.setStatus(ItemStatus.NORMAL);
            item.setUpdateTime(LocalDateTime.now());
            updateById(item);

            //2. 异步通知es，redis上架
            //TODO
            try {
                pubToEs(itemId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void down(Long itemId) {
        Item item = getById(itemId);
        if (item != null && item.getStatus() == ItemStatus.NORMAL) {
            item.setStatus(ItemStatus.DOWN);
            item.setUpdateTime(LocalDateTime.now());
            updateById(item);

            //2. 异步通知es，redis下架
            //TODO
            try {
                downToEs(itemId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(Long itemId) {
        Item item = getById(itemId);
        if (item != null) {
            item.setStatus(ItemStatus.DELETE);
            item.setUpdateTime(LocalDateTime.now());
            updateById(item);
        }
    }

    @Override
    public void pubToEs(Long itemId) throws IOException {
        Item item = getById(itemId);
        IndexRequest request = new IndexRequest("items").id(itemId.toString());

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("brand", item.getBrand());
        jsonMap.put("category", item.getCategory());
        jsonMap.put("commentCount", item.getCommentCount());
        jsonMap.put("id", item.getId());
        jsonMap.put("name", item.getName());
        jsonMap.put("price", item.getPrice());
        jsonMap.put("sold", item.getSold());
        jsonMap.put("status", item.getStatus().getValue());  // 使用枚举的数字值
        jsonMap.put("stock", item.getStock());

        // 处理 LocalDateTime 类型字段
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // 注册 JavaTimeModule 处理 LocalDateTime
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 以字符串格式输出时间

        // 将 LocalDateTime 转换为字符串格式
        jsonMap.put("updateTime", item.getUpdateTime());

        // 处理 image 字段（解析成 Map）
        Map<String, Object> imageMap = objectMapper.readValue(item.getImage(), Map.class);
        jsonMap.put("image", imageMap);

        // 转换成 JSON 并提交
        String jsonString = objectMapper.writeValueAsString(jsonMap);
        request.source(jsonString, XContentType.JSON);

        // 提交到 Elasticsearch
        client.index(request, RequestOptions.DEFAULT);
    }

    public void downToEs(Long itemId) throws IOException{
        DeleteRequest request = new DeleteRequest("items", itemId.toString());

        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        if (response.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            System.out.println("Document not found with id: " + itemId);
        } else {
            System.out.println("Document deleted successfully.");
        }
    }
}
