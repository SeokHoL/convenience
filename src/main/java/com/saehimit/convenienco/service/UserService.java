package com.saehimit.convenienco.service;

import com.saehimit.convenienco.dto.UsersDto;
import com.saehimit.convenienco.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void initializeAdmin() {
        // Admin 계정 존재 여부 확인
        if (userMapper.findByLoginId("admin") == null) { // findByLoginId 메서드 추가 필요
            UsersDto admin = UsersDto.builder()
                    .loginId("admin")
                    .password(passwordEncoder.encode("admin123")) // 비밀번호 암호화
                    .role("ADMIN")
                    .build();
            userMapper.createAdmin(admin);
        } else {
            System.out.println("Admin 계정이 이미 존재합니다.");
        }
    }
}
