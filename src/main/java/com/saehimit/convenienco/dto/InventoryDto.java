package com.saehimit.convenienco.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryDto {
    private int inventoryId;         // 재고 ID
    private String productCode;      // 품목 코드
    private String productName;      // 품목명
    private String productType;      // 품목 유형
    private String branch;           // 지점
    private String storageLocation;  // 보관소
    private int quantity;            // 현재 재고량
    private int incomingQuantity;    // 입고량
    private int outgoingQuantity;    // 출고량
    private LocalDate lastModified; //수정일자
}
