package com.saehimit.convenienco.controller;

import com.saehimit.convenienco.dto.ProductMasterDto;
import com.saehimit.convenienco.dto.SystemCodeDto;
import com.saehimit.convenienco.service.ProductMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/product_master")
@RequiredArgsConstructor
public class ProductMasterController {

    private final ProductMasterService productMasterService;


    @GetMapping
    public String showProductMasterPage(Model model) {
        model.addAttribute("products", productMasterService.getAllProducts());
        return "product_master"; // HTML 파일 이름
    }



    @PostMapping("/add")
    public String addProduct(@ModelAttribute ProductMasterDto productMasterDto) {
        //현재 로그인한 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        productMasterDto.setRegisteredBy(currentUsername); //등록자 설정
        productMasterService.addProduct(productMasterDto);
        return "redirect:/product_master";
    }

    @PostMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String productName,
            Model model
    ) {
        model.addAttribute("products", productMasterService.searchProducts(productType, productCode, productName));
        return "product_master";
    }

    //추가버튼
    @PostMapping("/addProduct")
    public String addCodeWithValidation(@ModelAttribute ProductMasterDto productMasterDto, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        productMasterDto.setRegisteredBy(currentUsername); // 등록자 설정
//        systemCodeDto.setModifiedBy(currentUsername);   // 수정자 설정

        try {
            productMasterService.addProductWithValidation(productMasterDto); // 중복 확인 후 추가
            return "redirect:/product_master";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("codes", productMasterService.getAllProducts());
            return "product_master";
        }
    }

    //수정 버튼
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute ProductMasterDto productMasterDto, Model model) {
        try {
            // 현재 로그인한 사용자 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            productMasterDto.setModifiedBy(currentUsername); // 수정자 설정
            productMasterService.updateProduct(productMasterDto);
            return "redirect:/product_master";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("products", productMasterService.getAllProducts());
            return "product_master"; // 오류 메시지를 포함하여 다시 렌더링
        }
    }


    @PostMapping("/delete")
    public String deleteProducts(@RequestParam("ids") List<Integer> ids) {
        productMasterService.deleteProducts(ids);
        return "redirect:/product_master";
    }


    @GetMapping("/check-duplicate-code")
    public ResponseEntity<Boolean> checkProductCodeDuplicate(@RequestParam String productCode) {
        boolean isDuplicate = productMasterService.isProductCodeDuplicate(productCode);
        return ResponseEntity.ok(isDuplicate);
    }

    @GetMapping("/check-duplicate-name")
    public ResponseEntity<Boolean> checkProductNameDuplicate(@RequestParam String productName) {
        boolean isDuplicate = productMasterService.isProductNameDuplicate(productName);
        return ResponseEntity.ok(isDuplicate);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ProductMasterDto>> getAllProducts() {
        List<ProductMasterDto> products = productMasterService.getAllProducts();
        return ResponseEntity.ok(products);
    }

}

