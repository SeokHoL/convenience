package com.saehimit.convenienco.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Data
public class PurchaseOrderItemDto {
    private int itemId;
    private String orderId;  // orderId 추가
    private String branch;   // branch 추가
    private String inventoryId; //재고번호
    private String productCode; 
    private String productName;
    private int price;
    private String unit;
    private int minOrder;
    private int maxOrder;
    private int orderQuantity;
    private LocalDate expectedDate;
}