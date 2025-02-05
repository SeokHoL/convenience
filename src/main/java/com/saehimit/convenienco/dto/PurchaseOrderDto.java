package com.saehimit.convenienco.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class PurchaseOrderDto {
    private String orderId;        // 발주번호
    private String branch;         // 지점명
    private String requesterId;    // 발주자 ID
    private String requesterName;  // 발주자 이름
    private String status = "미승인";    // 기본값(미승인으로)
    private LocalDate registeredDate;   // 등록일자
    private LocalDate lastModified;// 수정일자
    private String modifiedBy;     // 수정자

    private List<PurchaseOrderItemDto> items;

    // orderId 자동 생성 메서드 추가
    public void generateOrderId(String generatedOrderId) {
        this.orderId = generatedOrderId;
    }
}
