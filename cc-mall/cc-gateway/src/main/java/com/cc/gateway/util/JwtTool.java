package com.cc.gateway.util;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.cc.common.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.security.KeyPair;

@Component
public class JwtTool {

    private final JWTSigner jwtSigner;

    @Autowired
    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    public String createToken(Long userId, Duration ttl) {
        // 生成 JWT
        String token = JWT.create()
                .setPayload("user", userId)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();

        System.out.println("Generated Token: " + token);  // 打印生成的 Token
        return token;
    }

    public Long parseToken(String token) {
        if (token == null) {
            throw new UnauthorizedException("Not logged in");
        }

        JWT jwt;

        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid token", e);
        }


        if (!jwt.verify()) {
            System.out.println("Token verification failed: " + token);
            throw new UnauthorizedException("Invalid token");
        }

        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new UnauthorizedException("Token has expired");
        }

        Object userPayload = jwt.getPayload("user");
        if (userPayload == null) {
            throw new UnauthorizedException("Invalid token");
        }

        try {
            return Long.valueOf(userPayload.toString());
        } catch (RuntimeException e) {
            throw new UnauthorizedException("Invalid token");
        }
    }
}
