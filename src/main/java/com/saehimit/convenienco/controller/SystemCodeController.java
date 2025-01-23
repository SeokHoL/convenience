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
    private final SystemCodeService systemCodeService;

    public SystemCodeController(SystemCodeService service) {
        this.systemCodeService = service;
    }

    @GetMapping
    public String showSystemManagementPage(Model model) {
        model.addAttribute("codes", systemCodeService.getAllCodes());
        return "system_management";
    }

    @PostMapping("/search")
    public String searchCodes(
            @RequestParam(required = false) String codeIndex,
            @RequestParam(required = false) String codeValue,
            @RequestParam(required = false) String codeName,
            Model model
    ) {
        // 조회 결과 리스트
        List<SystemCodeDto> codes;

        // 세 필드가 모두 비어있으면 전체 조회
        if ((codeIndex == null || codeIndex.trim().isEmpty()) &&
                (codeValue == null || codeValue.trim().isEmpty()) &&
                (codeName == null || codeName.trim().isEmpty())) {
            codes = systemCodeService.getAllCodes(); // 전체 조회
        } else {
            // 조건 조회
            codes = systemCodeService.searchCodes(codeIndex, codeValue, codeName);
        }

        // 조회 결과를 모델에 추가
        model.addAttribute("codes", codes);

        // 뷰 반환
        return "system_management";
    }


    @PostMapping("/add")
    public String addCode(@ModelAttribute SystemCodeDto systemCodeDto) {
        // 현재 로그인한 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // 로그인한 사용자의 ID

        systemCodeDto.setModifiedBy(currentUsername); // 등록자를 로그인 사용자 ID로 설정
        systemCodeService.addCode(systemCodeDto);
        return "redirect:/system_management";
    }


    @PostMapping("/update")
    public String updateCode(SystemCodeDto systemCodeDto) {
        systemCodeService.updateCode(systemCodeDto);
        return "redirect:/system_management";
    }


    @PostMapping("/delete")
    public String deleteCodes(@RequestParam("codeIds") List<Integer> codeIds) {
        systemCodeService.deleteCode(codeIds); // 여러 개 삭제
        return "redirect:/system_management";
    }

    @PostMapping("/addCode")
    public String addCodeWithValidation(@ModelAttribute SystemCodeDto systemCodeDto, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        systemCodeDto.setModifiedBy(currentUsername);

        try {
            systemCodeService.addCodeWithValidation(systemCodeDto); // 중복 확인 후 추가
            return "redirect:/system_management";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("codes", systemCodeService.getAllCodes());
            return "system_management";
        }
    }





}