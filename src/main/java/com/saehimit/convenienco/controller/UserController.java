package com.saehimit.convenienco.controller;

import com.saehimit.convenienco.dto.UsersDto;
import com.saehimit.convenienco.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
        return "redirect:/user/search";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute UsersDto userDto) {
        userService.updateUser(userDto);
        return "redirect:/user/search";
    }
}

