package com.cc.item.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cc.common.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum ItemStatus {
    NORMAL(1, "Product already on shelves (normal)"),
    DOWN(2, "The product has been removed from the shelves"),
    DELETE(3, "Product deleted")
    ;
    @EnumValue
    int value;
    String desc;

    ItemStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ItemStatus of(int value) {
        if (value == 1) {
            return NORMAL;
        }
        if (value == 2) {
            return DOWN;
        }
        if (value == 3) {
            return DELETE;
        }
        throw new BadRequestException("Account status error");
    }
}