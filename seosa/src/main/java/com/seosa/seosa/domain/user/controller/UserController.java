package com.seosa.seosa.domain.user.controller;

import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.global.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/user/profile")
    public String getUserProfile(@AuthUser User user) {
        return "User Email: " + user.getEmail();
    }
}
