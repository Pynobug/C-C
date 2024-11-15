package com.cc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.common.exception.BadRequestException;
import com.cc.common.exception.ForbiddenException;
import com.cc.config.JwtProperties;
import com.cc.domain.dto.LoginFormDTO;
import com.cc.domain.po.User;
import com.cc.domain.vo.UserLoginVO;
import com.cc.enums.UserStatus;
import com.cc.mapper.UserMapper;
import com.cc.service.IUserService;
import com.cc.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    @Override
    public UserLoginVO login(LoginFormDTO loginFormDTO) {
        // 1.Data verify
        String username = loginFormDTO.getUsername();
        String password = loginFormDTO.getPassword();
        // 2.Query by username or mobile phone number
        User user = lambdaQuery().eq(User::getUsername, username).one();
        Assert.notNull(user, "User name error");
        // 3.Verify whether it is disabled
        if (user.getStatus() == UserStatus.FROZEN) {
            throw new ForbiddenException("User is frozen");
        }
        // 4.Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("The user name or password is incorrect");
        }
        // 5.Generate TOKEN
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        // 6.Encapsulated VO return
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setBalance(user.getBalance());
        vo.setToken(token);
        return vo;
    }
}
