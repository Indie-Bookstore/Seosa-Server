package com.seosa.seosa.domain.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
public class AdminController {

    @GetMapping("/admin")
    public String adminP() {
        return "admin Controller (관리자만 접속 가능)";
    }
}
