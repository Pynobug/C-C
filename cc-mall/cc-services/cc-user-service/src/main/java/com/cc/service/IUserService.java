package com.cc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cc.domain.dto.LoginFormDTO;
import com.cc.domain.dto.RegisterFormDTO;
import com.cc.domain.po.User;
import com.cc.domain.vo.UserLoginVO;

public interface IUserService extends IService<User> {

    Boolean register(RegisterFormDTO registerFormDTO);

    UserLoginVO login(LoginFormDTO loginFormDTO);
}
