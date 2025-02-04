package com.saehimit.convenienco.service;

import com.saehimit.convenienco.details.CustomUserDetails;
import com.saehimit.convenienco.dto.UsersDto;
import com.saehimit.convenienco.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    private final UserService userService;



    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        UsersDto user = userService.findByLoginId(loginId);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return new CustomUserDetails(user); // CustomUserDetails 반환
    }

}