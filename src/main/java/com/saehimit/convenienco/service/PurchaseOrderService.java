package com.saehimit.convenienco.service;

import com.saehimit.convenienco.dto.PurchaseOrderDto;
import com.saehimit.convenienco.dto.PurchaseOrderItemDto;
import com.saehimit.convenienco.dto.OrderStatus;
import com.saehimit.convenienco.mapper.PurchaseOrderMapper;
import com.saehimit.convenienco.mapper.PurchaseOrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;
    private final UserService userService;
    private final InventoryService inventoryService;

    public List<PurchaseOrderDto> searchPurchaseOrders(String branch, String orderId, String requesterName) {
        System.out.println("🔍 검색 실행 - branch: " + branch + ", orderId: " + orderId + ", requesterName: " + requesterName);

        List<PurchaseOrderDto> results = purchaseOrderMapper.searchPurchaseOrders(branch, orderId, requesterName);
        System.out.println("🔎 검색된 데이터 개수: " + results.size());

        return results;
    }


    public List<PurchaseOrderDto> getAllPurchaseOrders() {
        return purchaseOrderMapper.getAllPurchaseOrders();
    }
//public Set<PurchaseOrderDto> getAllPurchaseOrders() {
//    return new HashSet<>(purchaseOrderMapper.getAllPurchaseOrders());
//}


    public String generateOrderId() {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 가장 최근의 발주번호 숫자 조회
        Integer lastOrderNumber = purchaseOrderMapper.getLastOrderNumber();
        int nextOrderNumber = (lastOrderNumber != null) ? lastOrderNumber + 1 : 10001;

        return "PO" + datePart + "-" + nextOrderNumber;
    }
    @Transactional
    public String createNewOrder(String branch, String requesterId, String requesterName) {
        PurchaseOrderDto newOrder = new PurchaseOrderDto();
        newOrder.setOrderId(generateOrderId()); // 발주번호 생성
        newOrder.setBranch(branch);
        newOrder.setRequesterId(requesterId);
        newOrder.setRequesterName(requesterName);
        newOrder.setStatus("미승인");

        purchaseOrderMapper.insertPurchaseOrder(newOrder);
        System.out.println(" 생성된 발주번호: " + newOrder.getOrderId());

        return newOrder.getOrderId();
    }

    @Transactional
    public void addPurchaseOrder(PurchaseOrderDto orderDto) {
        if (orderDto.getOrderId() == null || orderDto.getOrderId().isEmpty()) {
            orderDto.setOrderId(generateOrderId());
        }

        if (orderDto.getStatus() == null || orderDto.getStatus().isEmpty()) {
            orderDto.setStatus("미승인");
        }

        if (orderDto.getBranch() == null || orderDto.getBranch().isEmpty()) {
            String branch = userService.getBranchByUserId(orderDto.getRequesterId());
            orderDto.setBranch(branch);
        }

        if (purchaseOrderMapper.findByOrderId(orderDto.getOrderId()) == null) {
            purchaseOrderMapper.insertPurchaseOrder(orderDto);
        }

        if (orderDto.getItems() != null) {
            for (PurchaseOrderItemDto item : orderDto.getItems()) {
                item.setOrderId(orderDto.getOrderId());

                //  품목별 재고번호 자동 생성 (orderId를 전달하도록 수정)
                String inventoryId = inventoryService.generateInventoryId(orderDto.getOrderId());
                item.setInventoryId(inventoryId);

                if (item.getBranch() == null || item.getBranch().isEmpty()) {
                    item.setBranch(orderDto.getBranch());
                }

                purchaseOrderItemMapper.insertPurchaseOrderItem(item);
            }
        }
    }


    // 특정 발주의 품목 조회
    public List<PurchaseOrderItemDto> getPurchaseOrderItems(String orderId) {
        return purchaseOrderItemMapper.findPurchaseOrderItemsByOrderId(orderId);
    }

    // 발주 품목 수량 수정 (발주수량만 변경 가능)
    @Transactional
    public void updatePurchaseOrderItems(List<PurchaseOrderItemDto> items) {
        if (items.isEmpty()) return;

        String modifiedBy = userService.getLoggedInUser(); // 로그인된 사용자 가져오기
        String orderId = purchaseOrderItemMapper.findOrderIdByItemId(items.get(0).getItemId());
        LocalDateTime now = LocalDateTime.now();

        for (PurchaseOrderItemDto item : items) {
            purchaseOrderItemMapper.updatePurchaseOrderItem(item);
        }

        purchaseOrderMapper.updatePurchaseOrderHeader(orderId, modifiedBy);
    }


    @Transactional
    public void deletePurchaseOrders(List<String> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 발주를 선택해주세요.");
        }

        // 1. 발주의 모든 품목 삭제
        purchaseOrderItemMapper.deletePurchaseOrderItems(orderIds);

        // 2. 발주 헤더 삭제
        purchaseOrderMapper.deletePurchaseOrder(orderIds);
    }



}