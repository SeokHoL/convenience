package com.saehimit.convenienco.dto;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersDto {

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
}
