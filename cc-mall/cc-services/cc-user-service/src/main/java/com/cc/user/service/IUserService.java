package com.cc.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.user.domain.dto.LoginFormDTO;
import com.cc.user.domain.dto.RegisterFormDTO;
import com.cc.user.domain.po.User;
import com.cc.user.domain.vo.UserLoginVO;

public interface IUserService extends IService<User> {

    Boolean register(RegisterFormDTO registerFormDTO);

    UserLoginVO login(LoginFormDTO loginFormDTO);
}
