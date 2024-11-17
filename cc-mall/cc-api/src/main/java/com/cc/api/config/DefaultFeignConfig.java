package com.cc.api.config;

import org.springframework.context.annotation.Bean;
import feign.Logger;

public class DefaultFeignConfig {
    @Bean
    public Logger.Level fullFeignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
