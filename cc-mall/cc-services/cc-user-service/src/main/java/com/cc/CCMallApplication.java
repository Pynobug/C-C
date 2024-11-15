package com.cc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.cc.mapper")
@SpringBootApplication
public class CCMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(CCMallApplication.class, args);
    }
}