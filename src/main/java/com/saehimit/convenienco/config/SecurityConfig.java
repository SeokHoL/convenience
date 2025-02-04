package com.saehimit.convenienco.config;

import com.saehimit.convenienco.handler.CustomFailureHandler;
import com.saehimit.convenienco.handler.CustomSuccessHandler;
import com.saehimit.convenienco.service.CustomUserDetailsService;
import com.saehimit.convenienco.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomFailureHandler customFailureHandler;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        // 정적 리소스 접근 허용
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/resources/**").permitAll()
                        // 로그인, 에러 페이지 접근 허용
                        .requestMatchers("/login", "/login?error", "/error","/user/info").permitAll()
                        // 관리자 전용 페이지
                        .requestMatchers("/register","system_management/**","/search").hasRole("ADMIN")
                        // 인증된 사용자 페이지
                        .requestMatchers("/main").hasAnyRole("ADMIN", "USER", "MANAGER")
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .successHandler(customSuccessHandler) // 로그인 성공 핸들러
                        .failureHandler(customFailureHandler) // 로그인 실패 핸들러
                        .defaultSuccessUrl("/main", true)
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }
}