package com.cc.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("cc-user-service")
public interface UserClient {
    @PutMapping("/users/money/deduct")
    default void deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount) {

    }
}