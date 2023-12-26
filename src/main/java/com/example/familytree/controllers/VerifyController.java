package com.example.familytree.controllers;

import com.example.familytree.enums.VerificationEnum;
import com.example.familytree.services.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VerifyController {
    private final UserAccountService userAccountService;


    @GetMapping("/register/verify")
    public String verifyUser(@RequestParam(name = "code") String code) {
        VerificationEnum verificationEnum = userAccountService.verify(code);

        switch (verificationEnum) {
            case SUCCESS -> {
                return "verify_success";
            }
            case FAILED -> {
                return "verify_fail";
            }
            case TIME_OUT -> {
                return "verify_time_out";
            }
        }
        return null;
    }
}
