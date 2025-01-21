package com.saehimit.convenienco.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @GetMapping("/login")
    public String login(String error, String logout, Model model) {
        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
        }
        if (logout != null) {
            model.addAttribute("message", "로그아웃 되었습니다.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String authenticate(String username, String password, Model model) {
        if ("admin".equals(username) && "admin123".equals(password)) { // 간단한 인증 로직
            return "redirect:/main"; // 인증 성공 시 main.html로 리다이렉트
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login"; // 인증 실패 시 login.html로 돌아감
        }
    }



    @GetMapping("/main")
    public String mainPage() {
        return "main";
    }
}
