package com.saehimit.convenienco.handler;

import com.saehimit.convenienco.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String loginId = authentication.getName(); // 현재 로그인한 사용자 ID
        userService.resetLoginFailCount(loginId); // 로그인 실패 횟수 초기화
        response.sendRedirect("/main"); // 로그인 성공 후 리다이렉트
    }
}