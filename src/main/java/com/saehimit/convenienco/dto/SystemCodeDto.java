package com.saehimit.convenienco.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemCodeDto {
    private int codeId;
    private String codeIndex;
    private String codeValue;
    private String codeName;
    private boolean active; // 사용 여부
    private String registeredBy; // 등록자
    private String modifiedBy; //수정자
    private LocalDateTime registeredDate; // 등록 날짜
    private LocalDateTime lastModified;  // 수정 날짜
}
