package com.saehimit.convenienco.controller;

import com.saehimit.convenienco.details.CustomUserDetails;
import com.saehimit.convenienco.dto.ProductMasterDto;
import com.saehimit.convenienco.dto.PurchaseOrderDto;
import com.saehimit.convenienco.dto.PurchaseOrderItemDto;
import com.saehimit.convenienco.dto.UsersDto;
import com.saehimit.convenienco.service.ProductMasterService;
import com.saehimit.convenienco.service.PurchaseOrderService;
import com.saehimit.convenienco.service.SystemCodeService;
import com.saehimit.convenienco.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("purchase_order")
@RequiredArgsConstructor
public class PuchaseOrderController {

    private final ProductMasterService productMasterService;
    private final PurchaseOrderService purchaseOrderService;
    private final UserService userService;
    private final SystemCodeService systemCodeService;

    // 발주 페이지 조회 (기존 showProductMasterPage 삭제)
    @GetMapping
    public String getPurchaseOrders(Model model) {
        List<PurchaseOrderDto> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();

        // 공통 코드 맵 가져오기
        Map<String, Map<String, String>> commonCodeMap = systemCodeService.getCommonCodeMap();

        // 발주상태 맵 (발주 상태 코드 -> 공통 코드명 매핑)
        Map<String, String> purchaseStatusMap = commonCodeMap.get("발주상태");

        // 상태 코드를 공통 코드명으로 변환
        for (PurchaseOrderDto order : purchaseOrders) {
            String statusCode = order.getStatus();
            if (purchaseStatusMap != null && purchaseStatusMap.containsKey(statusCode)) {
                order.setStatus(purchaseStatusMap.get(statusCode)); // C -> 승인대기, D -> 취소 등 변환
            }
        }

        model.addAttribute("purchaseOrders", purchaseOrders);
        model.addAttribute("purchaseStatusMap", purchaseStatusMap);

        return "purchase_order";
    }


    //상단바 검색
    @PostMapping("/search")
    public String searchPurchaseOrders(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String requesterName,
            Model model) {

        System.out.println("검색 실행 - branch: " + branch + ", orderId: " + orderId + ", requesterName: " + requesterName);

        List<PurchaseOrderDto> purchaseOrders = purchaseOrderService.searchPurchaseOrders(branch, orderId, requesterName);

        // 공통 코드 변환 로직 추가 (메인 화면과 동일하게 적용)
        Map<String, Map<String, String>> commonCodeMap = systemCodeService.getCommonCodeMap();
        Map<String, String> purchaseStatusMap = commonCodeMap.get("발주상태");

        for (PurchaseOrderDto order : purchaseOrders) {
            if (order != null) { // order가 null인지 확인
                String statusCode = order.getStatus();
                if (purchaseStatusMap != null && purchaseStatusMap.containsKey(statusCode)) {
                    order.setStatus(purchaseStatusMap.get(statusCode)); // C -> 승인대기, D -> 취소 등 변환
                }
            }
        }

        model.addAttribute("purchaseOrders", purchaseOrders);
        model.addAttribute("orderId", orderId);

        return "purchase_order";
    }



    @GetMapping("/generateOrderId")
    public ResponseEntity<Map<String, String>> generateOrderId() {
        String orderId = purchaseOrderService.generateOrderId();
        PurchaseOrderDto newOrder = new PurchaseOrderDto();
        newOrder.setOrderId(orderId);
        return ResponseEntity.ok(Map.of("orderId", orderId));
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addPurchaseOrder(
            @RequestBody PurchaseOrderDto orderDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        String requesterId = userDetails.getLoginId();
        String requesterName = userDetails.getRealUsername();
        String branch = userService.getBranchByUserId(requesterId);

        orderDto.setRequesterId(requesterId);
        orderDto.setRequesterName(requesterName);
        orderDto.setBranch(branch);

        purchaseOrderService.addPurchaseOrder(orderDto);

        return ResponseEntity.ok(Map.of("message", "발주가 성공적으로 등록되었습니다."));
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createNewOrder(@RequestBody Map<String, String> request) {
        String branch = request.get("branch");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();  // 로그인된 사용자 ID

        String orderId = purchaseOrderService.createNewOrder(branch, username, "관리자");
        Map<String, String> response = new HashMap<>();
        response.put("orderId", orderId);
        return ResponseEntity.ok(response);
    }



    // 특정 발주의 품목 목록 조회 API
    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getOrderItems(@RequestParam String orderId) {
        List<PurchaseOrderItemDto> orderItems = purchaseOrderService.getPurchaseOrderItems(orderId);
        Map<String, Map<String, String>> commonCodeMap = systemCodeService.getCommonCodeMap();
        Map<String, String> itemStatusMap = commonCodeMap.get("발주품목상태");

        return ResponseEntity.ok(Map.of("orderItems", orderItems, "itemStatusMap", itemStatusMap));
    }


    // 발주 품목 수정 API (발주수량만 변경 가능)
    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updatePurchaseOrderItems(@RequestBody List<PurchaseOrderItemDto> items) {
        purchaseOrderService.updatePurchaseOrderItems(items);
        return ResponseEntity.ok(Map.of("message", "발주가 성공적으로 수정되었습니다."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deletePurchaseOrders(@RequestBody Map<String, List<String>> request) {
        List<String> orderIds = request.get("orderIds");

        if (orderIds == null || orderIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "삭제할 발주를 선택해주세요."));
        }

        purchaseOrderService.deletePurchaseOrders(orderIds);
        return ResponseEntity.ok(Map.of("message", "선택한 발주가 성공적으로 삭제되었습니다."));
    }








}