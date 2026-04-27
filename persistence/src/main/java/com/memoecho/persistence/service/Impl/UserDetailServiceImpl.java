package com.memoecho.persistence.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memoecho.persistence.mapper.UserMessageMapper;
import com.memoecho.persistence.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserMessageMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.parseLong(username));
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        return new org.springframework.security.core.userdetails.User(
            user.getUserId().toString(),
            user.getPassword(),
            !user.isKill(),
            true,
            true,
            true,
            Collections.singletonList(
                new SimpleGrantedAuthority(
                    "ROLE_" + (user.getUserLevel() == 10 ? "ADMIN" : "USER")
                )
            )
        );
    }
}
