package com.saehimit.convenienco.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductMasterDto {
    private int masterId;           // 품목 마스터 ID
    private String productType;     // 품목 유형
    private String productCode;     // 품목 코드
    private String productName;     // 품목명
    private String unit;            // 단위
    private boolean active;
    private int price;              // 가격
    private String registeredBy;    // 등록자
    private LocalDate registeredDate; // 등록 일자
    private LocalDate lastModified;  // 수정 일자
    private String modifiedBy;      // 수정자 ID



}