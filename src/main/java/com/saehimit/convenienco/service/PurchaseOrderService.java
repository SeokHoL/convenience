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
    private final SystemCodeService systemCodeService;

    public List<PurchaseOrderDto> searchPurchaseOrders(String branch, String orderId, String requesterName) {
        System.out.println("검색 실행 - branch: " + branch + ", orderId: " + orderId + ", requesterName: " + requesterName);

        List<PurchaseOrderDto> results = purchaseOrderMapper.searchPurchaseOrders(branch, orderId, requesterName);

        // 검색 결과에도 상태 변환 적용
        Map<String, Map<String, String>> commonCodeMap = systemCodeService.getCommonCodeMap();
        Map<String, String> purchaseStatusMap = commonCodeMap.get("발주상태");

        for (PurchaseOrderDto order : results) {
            if (order != null) {
                String statusCode = order.getStatus();
                if (purchaseStatusMap != null && purchaseStatusMap.containsKey(statusCode)) {
                    order.setStatus(purchaseStatusMap.get(statusCode)); // C -> 승인대기
                }
            }
        }

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
        newOrder.setOrderId(generateOrderId());
        newOrder.setBranch(branch);
        newOrder.setRequesterId(requesterId);
        newOrder.setRequesterName(requesterName);

        // 발주 상태를 공통 코드에서 가져와 설정
        newOrder.setStatus(purchaseOrderMapper.getStatusCodeByName("승인대기"));

        purchaseOrderMapper.insertPurchaseOrder(newOrder);
        return newOrder.getOrderId();
    }


    @Transactional
    public void addPurchaseOrder(PurchaseOrderDto orderDto) {
        if (orderDto.getOrderId() == null || orderDto.getOrderId().isEmpty()) {
            orderDto.setOrderId(generateOrderId());
        }

        if (orderDto.getStatus() == null || orderDto.getStatus().isEmpty()) {
            // 공통 코드에서 '승인대기' 상태의 코드 값(C)을 가져옴
            String pendingApprovalCode = purchaseOrderMapper.getStatusCodeByName("승인대기");
            orderDto.setStatus(pendingApprovalCode);
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

                // 품목별 재고번호 자동 생성
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
            if (item.getMaxOrder() == null || item.getMaxOrder() == 0) {
                item.setMaxOrder(20); // 기본값 20 설정
            }
            purchaseOrderItemMapper.updatePurchaseOrderItem(item); //
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