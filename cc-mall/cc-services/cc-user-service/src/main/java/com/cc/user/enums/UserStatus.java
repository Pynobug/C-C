package com.cc.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cc.common.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum UserStatus {
    FROZEN(0, "Prohibition of use"),
    NORMAL(1, "Activated"),
    ;
    @EnumValue
    int value;
    String desc;

    UserStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserStatus of(int value) {
        if (value == 0) {
            return FROZEN;
        }
        if (value == 1) {
            return NORMAL;
        }
        throw new BadRequestException("Account status error");
    }
}