package com.saehimit.convenienco.service;

import com.saehimit.convenienco.mapper.PurchaseOrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final PurchaseOrderItemMapper purchaseOrderItemMapper;

    // 재고번호 자동 생성 (ST1001, ST1002, ...)
    public String generateInventoryId(String productCode) {
        // 해당 품목의 기존 재고번호 조회
        String existingInventoryId = purchaseOrderItemMapper.findInventoryIdByProductCode(productCode);

        if (existingInventoryId != null) {
            return existingInventoryId; // 기존 재고번호 유지
        }

        // 가장 마지막 재고번호 조회
        String lastInventoryId = purchaseOrderItemMapper.findLastInventoryId();
        int nextId = (lastInventoryId != null) ? Integer.parseInt(lastInventoryId.substring(2)) + 1 : 1001;

        return "ST" + String.format("%04d", nextId);
    }
}
