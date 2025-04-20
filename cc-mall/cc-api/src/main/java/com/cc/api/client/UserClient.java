package com.cc.api.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cc-user-service")
public interface UserClient {
}