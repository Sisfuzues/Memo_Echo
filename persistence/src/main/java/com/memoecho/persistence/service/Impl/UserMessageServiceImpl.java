package com.memoecho.persistence.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memoecho.persistence.dto.LoginRequest;
import com.memoecho.persistence.dto.LoginResponse;
import com.memoecho.persistence.dto.RegisterRequest;
import com.memoecho.persistence.dto.RegisterResponse;
import com.memoecho.persistence.mapper.UserMessageMapper;
import com.memoecho.persistence.pojo.User;
import com.memoecho.persistence.service.UserMessageService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Builder
public class UserMessageServiceImpl implements UserMessageService {
    @Autowired
    private UserMessageMapper userMapper;

    @Override
    public RegisterResponse UserRegister(RegisterRequest registerRequest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", registerRequest.getUserId());
        User userSelected = userMapper.selectOne(queryWrapper);
        if (userSelected != null){
            return new RegisterResponse("用户已存在");
        }

        User user = new User();
        user.setUserId(registerRequest.getUserId());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        userMapper.insert(user);
        return new RegisterResponse("注册成功");

    }

    @Override
    public LoginResponse UserLogin(LoginRequest LoginRequest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", LoginRequest.getUserId());
        User userSelected = userMapper.selectOne(queryWrapper);


        if (userSelected != null && userSelected.getUserId() != null){
            if (userSelected.getPassword().equals(LoginRequest.getPassword())){
                String token = "token";
                return new LoginResponse(null,token,"登录成功");
            }
        }
        return new LoginResponse(null,null,"用户不存在");
    }
}
