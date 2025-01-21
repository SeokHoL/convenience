package com.saehimit.convenienco;

import com.saehimit.convenienco.service.UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.saehimit.convenienco.mapper") // Mapper 경로
public class ConveniencoApplication implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public ConveniencoApplication(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ConveniencoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        userService.initializeAdmin();
    }
}