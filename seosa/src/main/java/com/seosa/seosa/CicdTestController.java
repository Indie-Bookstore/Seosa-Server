package com.seosa.seosa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CicdTestController {

    // EC2 Ubuntu 환경에서 잘 실행되는지 테스트
    @GetMapping("/cicd")
    public String index() {
        return "인증이 필요없는 URL 패턴 목록에 cicd 페이지 추가";
    }
}
