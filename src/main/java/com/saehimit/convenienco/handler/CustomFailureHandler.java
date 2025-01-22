package com.saehimit.convenienco.handler;

import com.saehimit.convenienco.dto.UsersDto;
import com.saehimit.convenienco.mapper.UserMapper;
import com.saehimit.convenienco.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class CustomFailureHandler implements AuthenticationFailureHandler {

    private final UserMapper userMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String loginId = request.getParameter("username");
        String errorMessage = "로그인 실패: ";

        try {
            UsersDto user = userMapper.findUserByLoginId(loginId);

            if (exception instanceof LockedException) {
                errorMessage = "계정이 잠겼습니다. 관리자에게 문의하세요.";
            } else if (user == null) {
                errorMessage = "존재하지 않는 아이디입니다.";
            } else if (exception.getMessage().contains("자격 증명에 실패하였습니다.")) {
                System.out.println("자격 증명 실패 감지됨");
                userMapper.incrementLoginFailCount(loginId);
                user = userMapper.findUserByLoginId(loginId);

                if (user.getLoginFailCount() >= 5) {
                    userMapper.lockAccount(loginId);
                    errorMessage = "로그인 실패가 5회를 초과하여 계정이 잠겼습니다.";
                } else {
                    errorMessage = "비밀번호가 틀렸습니다. (로그인 실패 횟수: " + user.getLoginFailCount() + "/5)";
                }
            } else {
                System.out.println("Exception message: " + exception.getMessage());
                errorMessage = "알 수 없는 오류가 발생했습니다.";
            }
        } catch (Exception e) {
            errorMessage = "시스템 오류가 발생했습니다.";
        }

        // 세션에 메시지 저장
        request.getSession().setAttribute("error", errorMessage);
        response.sendRedirect("/login?error");
    }

}