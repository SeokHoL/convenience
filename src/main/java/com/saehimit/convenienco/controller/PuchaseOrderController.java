package com.saehimit.convenienco.controller;

import com.saehimit.convenienco.dto.ProductMasterDto;
import com.saehimit.convenienco.dto.PurchaseOrderDto;
import com.saehimit.convenienco.dto.PurchaseOrderItemDto;
import com.saehimit.convenienco.dto.UsersDto;
import com.saehimit.convenienco.service.ProductMasterService;
import com.saehimit.convenienco.service.PurchaseOrderService;
import com.saehimit.convenienco.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public String showProductMasterPage(Model model) {
        model.addAttribute("products", productMasterService.getAllProducts());
        return "purchase_order";
    }

    //상단바 검색
    @PostMapping("/search")
    public String searchPurchaseOrders(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String requesterName,
            Model model) {

        System.out.println("🔍 검색 실행 - branch: " + branch + ", orderId: " + orderId + ", requesterName: " + requesterName);

        if (orderId != null) {
            System.out.println("✅ orderId 길이: " + orderId.length());
            System.out.println("✅ orderId의 HEX 값: " + orderId.chars()
                    .mapToObj(c -> String.format("%02X", c))
                    .reduce("", (a, b) -> a + " " + b));
        }

        List<PurchaseOrderDto> purchaseOrders = purchaseOrderService.searchPurchaseOrders(branch, orderId, requesterName);
        System.out.println("🔎 검색된 데이터 개수: " + purchaseOrders.size());

        model.addAttribute("purchaseOrders", purchaseOrders);
        model.addAttribute("orderId", orderId);

        return "purchase_order";
    }






    @PostMapping("/add")
    public ResponseEntity<?> addPurchaseOrder(@RequestBody PurchaseOrderDto newOrder) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();  // 로그인된 사용자 ID 가져오기

        // 기존 발주번호가 없으면 새로 생성
        if (newOrder.getOrderId() == null || newOrder.getOrderId().isEmpty()) {
            newOrder.setOrderId(purchaseOrderService.generateOrderId());
        }

        newOrder.setRequesterId(username);
        newOrder.setRequesterName("관리자"); // 관리자 임시 지정
        if (newOrder.getStatus() == null || newOrder.getStatus().isEmpty()) {
            newOrder.setStatus("미승인");
        }

        purchaseOrderService.addPurchaseOrder(newOrder);
        return ResponseEntity.ok(Map.of("message", "발주가 정상적으로 처리되었습니다."));
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

    @GetMapping("/generateOrderId")
    public ResponseEntity<Map<String, String>> generateOrderId() {
        String orderId = purchaseOrderService.generateOrderId();
        Map<String, String> response = new HashMap<>();
        response.put("orderId", orderId);
        return ResponseEntity.ok(response);
    }


}
