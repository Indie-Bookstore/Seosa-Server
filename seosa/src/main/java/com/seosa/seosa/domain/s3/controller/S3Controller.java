package com.seosa.seosa.domain.s3.controller;

import com.seosa.seosa.domain.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "S3 API", description = "Presigned URL 관련 API")
public class S3Controller {

    private final S3Service s3Service;

    // Presigned 생성 요청
    @GetMapping("/s3/presigned/{fileName:.+}")
    @Operation(summary = "Presigned URL 요청", description = "클라이언트측에서 Presigned URL을 요청합니다.")
    public Map<String, String> getPresignedUrl(
            @PathVariable(name = "fileName") @Schema(description = "확장자명을 포함해주세요")
            String fileName) {

        return s3Service.getPresignedUrl("images", fileName);
    }


}

