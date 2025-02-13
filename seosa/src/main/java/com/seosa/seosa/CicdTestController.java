package com.seosa.seosa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CicdTestController {

    // EC2 Ubuntu 환경에서 잘 실행되는지 테스트
    @GetMapping("/")
    public String index() {
        return "Hello World";
    }
}
