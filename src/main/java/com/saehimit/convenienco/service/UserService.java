package com.saehimit.convenienco.service;

import com.saehimit.convenienco.dto.UsersDto;
import com.saehimit.convenienco.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;



    public void initializeAdmin() {
        if (userMapper.findByLoginId("admin") == null) {
            UsersDto admin = UsersDto.builder()
                    .loginId("admin")
                    .username("관리자")
//                    .branch("본사")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ROLE_ADMIN")
                    .build();

            System.out.println("🔍 관리자 계정 생성 시 username: " + admin.getUsername());
            userMapper.createAdmin(admin);
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
        // modifier 파라미터 제거하고 이미 설정된 modifiedBy 사용
        userMapper.updateUser(userDto);
    }

    public void updateUserWithModifier(UsersDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName(); // 로그인 ID 가져오기

        userDto.setModifiedBy(loginId); // 수정자 설정
        userMapper.updateUser(userDto);
    }


    public void unlockAccount(String loginId) {
        userMapper.unlockAccount(loginId); // 계정 잠금 해제
        userMapper.resetLoginFailCount(loginId); // 로그인 실패 횟수 초기화
    }
    public void resetLoginFailCount(String loginId) {
        userMapper.resetLoginFailCount(loginId);
    }


    public UsersDto findByLoginId(String loginId) {
        return userMapper.findByLoginId(loginId);
    }


    public String getUserNameById(String userId) {
        return userMapper.findUserNameById(userId);
    }

    public String getBranchByUserId(String userId) {
        return userMapper.findBranchByUserId(userId); // DB에서 branch 조회
    }

    public String getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("로그인된 사용자가 없습니다.");
        }
        return authentication.getName(); // 현재 로그인한 사용자의 ID 반환
    }

//    public void handleLoginFailure(String loginId) {
//        UsersDto user = userMapper.findUserByLoginId(loginId);
//        if (user == null) {
//            throw new UsernameNotFoundException("아이디가 존재하지 않습니다.");
//        }
//        if (user.isAccountLocked()) {
//            throw new IllegalStateException("계정이 잠겼습니다. 관리자에게 문의하세요.");
//        }
//
//        userMapper.incrementLoginFailCount(loginId);
//        if (user.getLoginFailCount() + 1 >= 5) {
//            userMapper.lockAccount(loginId);
//            throw new IllegalStateException("로그인 실패가 5회를 초과하여 계정이 잠겼습니다.");
//        }
//    }
//
//    public UsersDto findByLoginId(String loginId) {
//        return userMapper.findUserByLoginId(loginId);
//    }
}

