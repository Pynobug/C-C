package com.cc.cart;

import com.cc.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@MapperScan("com.cc.cart.mapper")
@EnableFeignClients(basePackages = "com.cc.api.client" , defaultConfiguration = DefaultFeignConfig.class) // 局部扫描配置
@SpringBootApplication
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}