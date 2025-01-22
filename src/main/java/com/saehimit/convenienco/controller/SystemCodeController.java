package com.saehimit.convenienco.controller;

import com.saehimit.convenienco.dto.SystemCodeDto;
import com.saehimit.convenienco.service.SystemCodeService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/system_management")
public class SystemCodeController {
    private final SystemCodeService service;

    public SystemCodeController(SystemCodeService service) {
        this.service = service;
    }

    @GetMapping
    public String showSystemManagementPage(Model model) {
        model.addAttribute("codes", service.getAllCodes());
        return "system_management";
    }

    @PostMapping("/search")
    public String searchCodes(@RequestParam(required = false) String codeIndex, Model model) {
        List<SystemCodeDto> codes = (codeIndex == null || codeIndex.isEmpty())
                ? service.getAllCodes()
                : service.getCodesByIndex(codeIndex);
        model.addAttribute("codes", codes);
        return "system_management";
    }

    @PostMapping("/add")
    public String addCode(@ModelAttribute SystemCodeDto systemCodeDto) {
        // 현재 로그인한 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // 로그인한 사용자의 ID

        systemCodeDto.setModifiedBy(currentUsername); // 등록자를 로그인 사용자 ID로 설정
        service.addCode(systemCodeDto);
        return "redirect:/system_management";
    }


    @PostMapping("/update")
    public String updateCode(SystemCodeDto systemCodeDto) {
        service.updateCode(systemCodeDto);
        return "redirect:/system_management";
    }
}