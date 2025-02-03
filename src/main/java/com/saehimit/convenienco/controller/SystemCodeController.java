package com.saehimit.convenienco.controller;
//다른곳에서 (예를들면 PostMapping) 에서 redirect:/system_management를 사용하면 클라이언트가 새로운 GET 요청을 보내고, 서버가 이를 처리하기 위해 @GetMapping 메서드를 실행
//String을 반환하면 뷰 이름으로 간주 //템플릿 엔진(예: Thymeleaf, JSP 등)을 통해 HTML 파일을 렌더링하고 클라이언트에 반환
//ResponseEntity<?>는 HTTP 응답 자체를 직접 정의할 수 있음. / HTTP 200 응답과 JSON 데이터 반환
//redirect 없으면 그냥 system_management 만 반환함. 그냥 썡
import com.saehimit.convenienco.dto.SystemCodeDto;
import com.saehimit.convenienco.service.SystemCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// redirect 는 ->
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

        systemCodeDto.setRegisteredBy(currentUsername); // 등록자 설정
//        systemCodeDto.setModifiedBy(currentUsername);   // 수정자 설정
        systemCodeService.addCode(systemCodeDto);
        return "redirect:/system_management";
    }

    //추가버튼
    @PostMapping("/addCode")
    public String addCodeWithValidation(@ModelAttribute SystemCodeDto systemCodeDto, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        systemCodeDto.setRegisteredBy(currentUsername); // 등록자 설정
//        systemCodeDto.setModifiedBy(currentUsername);   // 수정자 설정

        try {
            systemCodeService.addCodeWithValidation(systemCodeDto); // 중복 확인 후 추가
            return "redirect:/system_management";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("codes", systemCodeService.getAllCodes());
            return "system_management";
        }
    }

    //수정버튼
    @PostMapping("/update")
    public String updateCode(@ModelAttribute SystemCodeDto systemCodeDto, Model model) {
        try {
            // 중복 검증 및 업데이트 처리
            systemCodeService.updateCodeWithValidation(systemCodeDto);
        } catch (IllegalArgumentException e) {
            // 에러 메시지와 기존 데이터를 모델에 설정
            model.addAttribute("error", e.getMessage());
            model.addAttribute("codes", systemCodeService.getAllCodes());
            return "system_management"; // 에러 발생 시 다시 페이지 로드
        }
        return "redirect:/system_management";
    }



    @PostMapping("/delete")
    public String deleteCodes(@RequestParam("codeIds") List<Integer> codeIds) {
        systemCodeService.deleteCode(codeIds); // 여러 개 삭제
        return "redirect:/system_management";
    }

    @GetMapping("/check-duplicate-code")
    public ResponseEntity<Boolean> checkCodeValueDuplicate(@RequestParam String codeValue) {
        boolean isDuplicate = systemCodeService.isCodeValueDuplicate(codeValue);
        return ResponseEntity.ok(isDuplicate);
    }

    @GetMapping("/check-duplicate-name")
    public ResponseEntity<Boolean> checkCodeNameDuplicate(@RequestParam String codeName) {
        boolean isDuplicate = systemCodeService.isCodeNameDuplicate(codeName);
        return ResponseEntity.ok(isDuplicate);
    }



}





