package com.memoecho.persistence.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memoecho.persistence.dto.*;
import com.memoecho.persistence.mapper.UserMessageMapper;
import com.memoecho.persistence.pojo.User;
import com.memoecho.persistence.service.CodeMessageService;
import com.memoecho.persistence.service.UserMessageService;
import com.memoecho.persistence.util.JwtUtil;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Builder
public class UserMessageServiceImpl implements UserMessageService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMessageMapper userMapper;

    @Autowired
    private CodeMessageService codeMessageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse UserRegister(RegisterRequest registerRequest) throws AuthenticationException{

        EmailResponse emailResponse = codeMessageService.verifyCode(registerRequest.getEmail(), registerRequest.getCode());
        if (emailResponse.getCode().equals("500")){
            return new RegisterResponse(emailResponse.getMessage());
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", registerRequest.getUserId());
        User userSelected = userMapper.selectOne(queryWrapper);
        if (userSelected != null){
            return new RegisterResponse("用户已存在");
        }

        User user = new User();
        user.setUserId(registerRequest.getUserId());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setUserLevel(1);
        user.setKill(false);

        userMapper.insert(user);

        codeMessageService.deleteCode(registerRequest.getEmail());
        return new RegisterResponse("注册成功");

    }

    @Override
    public LoginResponse UserLogin(LoginRequest loginRequest) throws AuthenticationException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserId(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.createToken(loginRequest.getUserId().toString());
        return new LoginResponse(loginRequest.getUserId(), jwt, "登录成功");
    }
}
