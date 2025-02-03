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
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;

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

        Random random = new Random();
        int randomNumber = 10000 + random.nextInt(90000); // 10000 ~ 99999 사이 랜덤 숫자

        return "PO" + datePart + "-" + randomNumber;
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
        System.out.println("📌 생성된 발주번호: " + newOrder.getOrderId());

        return newOrder.getOrderId();
    }

    @Transactional
    public void addPurchaseOrder(PurchaseOrderDto orderDto) {
        // 1️⃣ 발주번호가 없으면 생성
        if (orderDto.getOrderId() == null || orderDto.getOrderId().isEmpty()) {
            orderDto.setOrderId(generateOrderId());
        }

        // 2️⃣ 기본값 설정
        if (orderDto.getStatus() == null || orderDto.getStatus().isEmpty()) {
            orderDto.setStatus("미승인");
        }

        // 3️⃣ 구매 발주 (헤더) 저장 (중복 방지)
        if (purchaseOrderMapper.findByOrderId(orderDto.getOrderId()) == null) {
            purchaseOrderMapper.insertPurchaseOrder(orderDto);
        }

        // 4️⃣ 구매 발주 상세 (아이템) 저장
        if (orderDto.getItems() != null) {
            for (PurchaseOrderItemDto item : orderDto.getItems()) {
                item.setOrderId(orderDto.getOrderId());
                purchaseOrderItemMapper.insertPurchaseOrderItem(item);
            }
        }
    }

}