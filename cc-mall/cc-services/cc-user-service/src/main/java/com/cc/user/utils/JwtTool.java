package com.cc.user.utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.cc.common.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTool {
    private final JWTSigner jwtSigner;

    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    /**
     * Example Create an access-token
     *
     * @param userId userId
     * @param ttl    token validity period
     * @return access-token
     */
    public String createToken(Long userId, Duration ttl) {
        // 1.Generate jws
        return JWT.create()
                .setPayload("user", userId)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();
    }

    /**
     * Resolve token
     *
     * @param token token
     * @return Resolve the user information obtained by refreshing the token
     */
    public Long parseToken(String token) {
        // 1.Check whether the token is empty
        if (token == null) {
            throw new UnauthorizedException("Not logged in");
        }
        // 2.Verify and parse jwt
        JWT jwt;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid token", e);
        }
        // 2.Check whether the jwt is valid
        if (!jwt.verify()) {
            // Verification failure
            throw new UnauthorizedException("Invalid token");
        }
        // 3.Check for expiration
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new UnauthorizedException("token has expired");
        }
        // 4.Data format check
        Object userPayload = jwt.getPayload("user");
        if (userPayload == null) {
            // Data is null
            throw new UnauthorizedException("Invalid token");
        }

        // 5.Data analysis
        try {
            return Long.valueOf(userPayload.toString());
        } catch (RuntimeException e) {
            // Incorrect data format
            throw new UnauthorizedException("Invalid token");
        }
    }
}