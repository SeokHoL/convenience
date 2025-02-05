package com.saehimit.convenienco.controller;


import com.saehimit.convenienco.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final PurchaseOrderService purchaseOrderService;



    @GetMapping("/main")
    public String mainPage(Model model) {
        model.addAttribute("contentTemplate", "fragments/default");
        return "main";
    }



}

