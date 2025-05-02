package com.cc.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public KeyPair keyPair(JwtProperties properties) {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(
                properties.getLocation(),
                properties.getPassword().toCharArray()
        );
        KeyPair keyPair = factory.getKeyPair(
                properties.getAlias(),
                properties.getPassword().toCharArray()
        );
        return keyPair;
    }

}