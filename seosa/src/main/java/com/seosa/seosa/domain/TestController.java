package com.seosa.seosa.domain;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("")
    public String test() {
        return "test ok";
    }

    @GetMapping("/redirectOrg")
    public void redirectOrg(HttpServletResponse response) throws IOException {

        // // 백엔드 api 연결
        // String uri = "http://localhost:8080/test/redirectDst";

        // 프론트 페이지 연결
        String uri = "http://localhost:8082/redirectDst";

        String targetUrl = UriComponentsBuilder.fromUriString(uri)
                .queryParam("accessToken", "test_accessToken")
                .queryParam("refreshToken", "test_refreshToken")
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }

    @GetMapping("/redirectDst")
    public String redirectDst(
            @RequestParam(required = false) String accessToken,
            @RequestParam(required = false) String refreshToken
    ) {
        System.out.println("accessToken = " + accessToken);
        System.out.println("refreshToken = " + refreshToken);
        return "리디렉션 성공! 콘솔에서 토큰 확인하세요.";
    }
}
