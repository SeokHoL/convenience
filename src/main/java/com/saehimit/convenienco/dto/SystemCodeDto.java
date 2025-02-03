package com.saehimit.convenienco.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SystemCodeDto {
    private int codeId;
    private String codeIndex; //색인코드(품목유형포함)
    private String codeValue; //공통코드
    private String codeName; //공통코드명
    private boolean active; // 사용 여부
    private String registeredBy; // 등록자
    private String modifiedBy; //수정자
    private LocalDate registeredDate; // 등록 날짜
    private LocalDate lastModified;  // 수정 날짜
}
