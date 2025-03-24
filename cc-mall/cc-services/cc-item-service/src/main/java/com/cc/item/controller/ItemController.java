package com.cc.item.controller;

import com.cc.item.domain.po.Item;
import com.cc.item.service.impl.ItemServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = "商品相关接口")
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;

    @ApiOperation("新增商品")
    @PostMapping("/add")
    public void addItem(@RequestBody Item item) {
        itemService.addItem(item);
    }


    @ApiOperation("上架商品")
    @PostMapping("/{itemId}/publish")
    public void publish(@PathVariable Long itemId) {
        itemService.publish(itemId);
    }

    @ApiOperation("下架商品")
    @PostMapping("/{itemId}/down")
    public void down(@PathVariable Long itemId) {
        itemService.down(itemId);
    }

    @ApiOperation("删除商品")
    @PostMapping("/{itemId}/delete")
    public void delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
    }
}
