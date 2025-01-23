package com.saehimit.convenienco.service;

import com.saehimit.convenienco.dto.UsersDto;
import com.saehimit.convenienco.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Autowired
    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        UsersDto user = userMapper.findByLoginId(loginId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with loginId: " + loginId);
        }
        System.out.println("Loaded user loginId: " + user.getLoginId());
        // UsersDto 자체가 UserDetails를 구현하므로 변환 없이 반환
        return user;
    }

}