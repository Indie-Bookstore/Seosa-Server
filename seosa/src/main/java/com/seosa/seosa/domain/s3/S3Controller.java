package com.seosa.seosa.domain.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/presign")
    public ResponseEntity<String> getPresignedUrl(@RequestParam String filename) {
        String url = s3Service.generatePresignedUrl(filename);
        return ResponseEntity.ok(url);
    }
}

