package com.saehimit.convenienco.service;

import com.saehimit.convenienco.dto.UsersDto;
import com.saehimit.convenienco.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (userMapper.findByLoginId("admin") == null) { // findByLoginId 메서드는 UserMapper에 정의되어야 함
            UsersDto admin = UsersDto.builder()
                    .loginId("admin")
                    .password(passwordEncoder.encode("admin123")) // 비밀번호 암호화
                    .role("ROLE_ADMIN")
                    .build();
            userMapper.createAdmin(admin); // createAdmin 메서드는 admin 계정을 저장
        } else {
            System.out.println("Admin 계정이 이미 존재합니다.");
        }
    }

    public void saveUser(UsersDto usersDto) {
        // 중복 확인
        if (userMapper.findUserByLoginId(usersDto.getLoginId()) != null) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }
        if (userMapper.findUserByEmail(usersDto.getEmail()) != null) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }
        if (userMapper.findUserByPhoneNumber(usersDto.getPhoneNumber()) != null) {
            throw new IllegalArgumentException("중복된 핸드폰 번호입니다.");
        }

        // 비밀번호 암호화
        usersDto.setPassword(passwordEncoder.encode(usersDto.getPassword()));

        // 사용자 저장
        userMapper.saveUser(usersDto);
    }

    public boolean isLoginIdAvailable(String loginId) {
        return userMapper.findUserByLoginId(loginId) == null;
    }

    public boolean isEmailAvailable(String email) {
        return userMapper.findUserByEmail(email) == null;
    }

    public boolean isPhoneNumberAvailable(String phoneNumber) {
        return userMapper.findUserByPhoneNumber(phoneNumber) == null;
    }

    public List<UsersDto> searchUsers(String loginId, String username, String branch) {
        return userMapper.searchUsers(loginId, username, branch);
    }

    public void deleteUsers(List<Long> loginIds) {
        userMapper.deleteUsers(loginIds);
    }

    public void updateUser(UsersDto userDto) {
        userMapper.updateUser(userDto);
    }
}