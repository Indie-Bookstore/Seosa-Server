package com.seosa.seosa.domain.faq.controller;

import com.seosa.seosa.domain.faq.dto.FAQRequestDTO;
import com.seosa.seosa.domain.faq.dto.FAQResponseDTO;
import com.seosa.seosa.domain.faq.service.FAQService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/faq")
@RequiredArgsConstructor
@Validated
public class FAQController {

    private final FAQService faqService;

    /**
     * FAQ 생성
     */
    @PostMapping
    @Operation(summary = "FAQ 생성", description = "새로운 FAQ를 생성합니다.")
    public ResponseEntity<FAQResponseDTO> createFAQ(@RequestBody @Validated FAQRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(faqService.createFAQ(requestDTO));
    }

    /**
     * 모든 FAQ 조회
     */
    @GetMapping
    @Operation(summary = "FAQ 목록 조회", description = "모든 FAQ를 조회합니다.")
    public ResponseEntity<List<FAQResponseDTO>> getAllFAQs() {
        return ResponseEntity.ok(faqService.getAllFAQs());
    }

    /**
     * 특정 FAQ 조회
     */
    @GetMapping("/{faqId}")
    @Operation(summary = "FAQ 단일 조회", description = "FAQ ID를 이용하여 특정 FAQ를 조회합니다.")
    public ResponseEntity<FAQResponseDTO> getFAQById(@PathVariable Long faqId) {
        return ResponseEntity.ok(faqService.getFAQById(faqId));
    }

    /**
     * FAQ 수정
     */
    @PutMapping("/{faqId}")
    @Operation(summary = "FAQ 수정", description = "FAQ 내용을 수정합니다.")
    public ResponseEntity<FAQResponseDTO> updateFAQ(@PathVariable Long faqId,
                                                    @RequestBody @Validated FAQRequestDTO requestDTO) {
        return ResponseEntity.ok(faqService.updateFAQ(faqId, requestDTO));
    }

    /**
     * FAQ 삭제
     */
    @DeleteMapping("/{faqId}")
    @Operation(summary = "FAQ 삭제", description = "FAQ를 삭제합니다.")
    public ResponseEntity<Map<String, String>> deleteFAQ(@PathVariable Long faqId) {
        faqService.deleteFAQ(faqId);
        return ResponseEntity.ok(Map.of("message", "FAQ가 삭제되었습니다."));
    }
}

