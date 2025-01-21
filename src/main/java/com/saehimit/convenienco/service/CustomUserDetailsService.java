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

        // 여기서 UserDetails 객체로 변환
        return User.builder()
                .username(user.getLoginId())
                .password(user.getPassword())
                .roles(user.getRole().replace("ROLE_", "")) // ROLE_ 제거
                .build();
    }
}