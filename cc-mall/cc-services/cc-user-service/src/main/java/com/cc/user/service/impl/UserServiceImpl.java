package com.cc.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.common.exception.BadRequestException;
import com.cc.common.exception.ForbiddenException;
import com.cc.user.config.JwtProperties;
import com.cc.user.domain.dto.LoginFormDTO;
import com.cc.user.domain.dto.RegisterFormDTO;
import com.cc.user.domain.po.User;
import com.cc.user.domain.vo.UserLoginVO;
import com.cc.user.enums.UserStatus;
import com.cc.user.mapper.UserMapper;
import com.cc.user.service.IUserService;
import com.cc.user.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    @Override
    public Boolean register(RegisterFormDTO registerFormDTO) {
        String username = registerFormDTO.getUsername();
        String password = registerFormDTO.getPassword();
        String confirmPassword = registerFormDTO.getConfirmPassword();
        String phone = registerFormDTO.getPhone();

        // 1. Check if passwords match
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match");
        }

        // 2. Check if username already exists
        User existingUser = lambdaQuery().eq(User::getUsername, username).one();
        if (Objects.nonNull(existingUser)) {
            throw new BadRequestException("Username already exists");
        }

        // 3. Create new user
        User newUser = new User();
        newUser.setUsername(username)
                .setPassword(passwordEncoder.encode(password)) // Encrypt password
                .setPhone(phone)
                .setStatus(UserStatus.NORMAL) // Set default status
                .setBalance(0) // Set initial balance
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());

        // 4. Save user to the database
        boolean isSaved = save(newUser);
        return isSaved;
    }

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
