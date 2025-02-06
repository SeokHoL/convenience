package com.saehimit.convenienco.mapper;

import com.saehimit.convenienco.dto.PurchaseOrderItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseOrderItemMapper {
    void insertPurchaseOrderItem(PurchaseOrderItemDto purchaseOrderItem);
    List<PurchaseOrderItemDto> findPurchaseOrderItemsByOrderId(@Param("orderId") String orderId);
    void updatePurchaseOrderItem(PurchaseOrderItemDto purchaseOrderItem);
    void deletePurchaseOrderItems(@Param("orderIds") List<String> orderIds);

    String findInventoryIdByProductCode(String productCode);

    String findLastInventoryId();

    String findLastInventoryIdByOrderId(String orderId);

    String findOrderIdByItemId(int itemId);
}