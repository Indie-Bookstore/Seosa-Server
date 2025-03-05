package com.seosa.seosa.domain.user.controller;

import com.seosa.seosa.domain.user.dto.CustomUserDetails;
import com.seosa.seosa.global.annotation.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/user/profile")
    public String getUserProfile(@Auth CustomUserDetails userDetails) {
        return "User Profile: " + userDetails.getEmail();
    }
}
