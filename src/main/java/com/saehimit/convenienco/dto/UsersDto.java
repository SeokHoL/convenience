package com.saehimit.convenienco.dto;


import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersDto implements UserDetails {

    private Long userId; // 식별자
    private String loginId;
    private String password;
    private String confirmPassword; // 비밀번호 확인 필드 추가
    private String username;
    private String role; // 권한
    private String email;
    private String branch;
    private String phoneNumber;
    private LocalDate joinDate; // 입사일자
    private LocalDate leaveDate; // 퇴사일자
    private boolean accountLocked; // 계정 잠금 여부
    private int loginFailCount; // 로그인 실패 횟수
    private LocalDate modifiedAt; // 수정 일시
    private String modifiedBy; // 수정자

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return loginId; // 반드시 loginId를 반환하도록 설정
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked; // `accountLocked`가 true일 경우 로그인 불가
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
