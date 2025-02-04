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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
            System.out.println("orderId 길이: " + orderId.length());
            System.out.println("orderId의 HEX 값: " + orderId.chars()
                    .mapToObj(c -> String.format("%02X", c))
                    .reduce("", (a, b) -> a + " " + b));
        }

        List<PurchaseOrderDto> purchaseOrders = purchaseOrderService.searchPurchaseOrders(branch, orderId, requesterName);
        System.out.println("검색된 데이터 개수: " + purchaseOrders.size());

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
    public ResponseEntity<Map<String, String>> addPurchaseOrder(@RequestBody PurchaseOrderDto orderDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        String requesterId = userDetails.getUsername();
        String requesterName = userService.getUserNameById(requesterId);
        String branch = userService.getBranchByUserId(requesterId);

        if (orderDto.getRequesterId() == null) {
            orderDto.setRequesterId(requesterId);
        }

        if (orderDto.getRequesterName() == null) {
            orderDto.setRequesterName(requesterName);
        }

        if (orderDto.getBranch() == null) {
            orderDto.setBranch(branch);
        }

        purchaseOrderService.addPurchaseOrder(orderDto);

        //  JSON 형식의 응답 반환
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



}
