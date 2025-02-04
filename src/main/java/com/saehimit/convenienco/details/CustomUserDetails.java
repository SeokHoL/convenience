package com.saehimit.convenienco.details;

import com.saehimit.convenienco.dto.UsersDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final UsersDto user;

    public CustomUserDetails(UsersDto user) {
        if (user == null) { //  방어 코드 추가
            throw new IllegalArgumentException("CustomUserDetails: UsersDto user가 null입니다!");
        }
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getLoginId() {
        if (user == null) { // 추가 확인
            throw new IllegalStateException("CustomUserDetails: user가 null이므로 getLoginId() 호출 불가");
        }
        return user.getLoginId();
    }

    public String getRealUsername() {
        return user.getUsername(); // ✅ 실제 username 반환 (DB에서 조회용)
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
