package com.saehimit.convenienco.controller;

import com.saehimit.convenienco.details.CustomUserDetails;
import com.saehimit.convenienco.dto.UsersDto;
import com.saehimit.convenienco.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "login";
    }

//    @PostMapping("/login")
//    public String authenticate(@RequestParam String username, @RequestParam String password, Model model) {
//        try {
//            // 서비스에서 인증 수행
//            UsersDto user = userService.authenticate(username, password);
//            return "redirect:/main"; // 인증 성공 시 리다이렉트
//        } catch (Exception e) {
//            model.addAttribute("error", e.getMessage()); // 예외 메시지 전달
//            return "login"; // 로그인 페이지로 돌아감
//        }
//    }


    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("usersDto", new UsersDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("usersDto") UsersDto usersDto, Model model) {
        try {
            userService.saveUser(usersDto); // 중복 확인 및 저장 처리
            model.addAttribute("message", "사용자 등록이 완료되었습니다.");
            return "redirect:/main";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/check/email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return userService.isEmailAvailable(email);
    }

    @GetMapping("/check/phone")
    @ResponseBody
    public boolean checkPhoneNumber(@RequestParam String phoneNumber) {
        return userService.isPhoneNumberAvailable(phoneNumber);
    }

    @GetMapping("/check/loginId")
    @ResponseBody
    public boolean checkLoginId(@RequestParam String loginId) {
        return userService.isLoginIdAvailable(loginId);
    }



    @GetMapping("/search")
    public String searchUsers(@RequestParam(required = false) String loginId,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) String branch,
                              Model model) {
        List<UsersDto> users = userService.searchUsers(loginId, username, branch);
        model.addAttribute("users", users);
        return "user_search";
    }

    @PostMapping("/delete")
    public String deleteUsers(@RequestParam List<Long> loginIds) {
        userService.deleteUsers(loginIds);
        return "redirect:/search";
    }


    @PostMapping("/update")
    public String updateUser(@ModelAttribute UsersDto userDto) {
        userService.updateUserWithModifier(userDto);
        return "redirect:/search";
    }




    @PostMapping("/unlock")
    public String unlockAccount(@RequestParam String loginId) {
        userService.unlockAccount(loginId);
        return "redirect:/search";
    }

    @GetMapping("/user/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String loginId = userDetails.getLoginId(); //  loginId 가져오기
        String realUsername = userDetails.getRealUsername();
        UsersDto user = userService.findByLoginId(loginId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        //  loginId와 username을 분리하여 반환
        Map<String, Object> response = new HashMap<>();
        response.put("loginId", user.getLoginId());
        response.put("username", realUsername);
        response.put("branch", user.getBranch() != null ? user.getBranch() : "지점 정보 없음");

        return ResponseEntity.ok(response);
    }





}



