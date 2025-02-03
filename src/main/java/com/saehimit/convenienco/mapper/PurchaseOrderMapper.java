package com.saehimit.convenienco.mapper;

import com.saehimit.convenienco.dto.PurchaseOrderDto;
import com.saehimit.convenienco.dto.PurchaseOrderItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseOrderMapper {
    void insertPurchaseOrder(PurchaseOrderDto purchaseOrder);

    List<PurchaseOrderDto> searchPurchaseOrders(
            @Param("branch") String branch,
            @Param("orderId") String orderId,
            @Param("requesterName") String requesterName
    );

    List<PurchaseOrderDto> getAllPurchaseOrders();

    PurchaseOrderDto findByOrderId(String orderId);
}