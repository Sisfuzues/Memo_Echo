package com.memoecho.persistence.service;

import com.memoecho.persistence.dto.LoginRequest;
import com.memoecho.persistence.dto.LoginResponse;
import com.memoecho.persistence.dto.RegisterRequest;
import com.memoecho.persistence.dto.RegisterResponse;
import com.memoecho.persistence.pojo.User;

public interface UserMessageService {
    User user = null;

    public RegisterResponse UserRegister(RegisterRequest registerRequest);
    public LoginResponse UserLogin(LoginRequest loginRequest);

}
