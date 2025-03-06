package com.cc.user.controller;

import com.cc.user.domain.dto.LoginFormDTO;
import com.cc.user.domain.dto.RegisterFormDTO;
import com.cc.user.domain.vo.UserLoginVO;
import com.cc.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "User-dependent interface")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @ApiOperation("User registration interface")
    @PostMapping("register")
    public Boolean register(@RequestBody @Validated RegisterFormDTO registerFormDTO) {
        return userService.register(registerFormDTO);
    }


    @ApiOperation("User login interface")
    @PostMapping("login")
    public UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO){
        return userService.login(loginFormDTO);
    }
}

