package com.seosa.seosa.domain.faq.service;

import com.seosa.seosa.domain.faq.dto.FAQRequestDTO;
import com.seosa.seosa.domain.faq.dto.FAQResponseDTO;
import com.seosa.seosa.domain.faq.entity.FAQ;
import com.seosa.seosa.domain.faq.repository.FAQRepository;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FAQService {

    private final FAQRepository faqRepository;

    /**
     * FAQ 생성
     */
    @Transactional
    public FAQResponseDTO createFAQ(FAQRequestDTO requestDTO) {
        FAQ faq = FAQ.builder()
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .build();
        return new FAQResponseDTO(faqRepository.save(faq));
    }

    /**
     * 모든 FAQ 조회
     */
    @Transactional(readOnly = true)
    public List<FAQResponseDTO> getAllFAQs() {
        return faqRepository.findAll().stream()
                .map(FAQResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 특정 FAQ 조회
     */
    @Transactional(readOnly = true)
    public FAQResponseDTO getFAQById(Long faqId) {
        FAQ faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new CustomException(ErrorCode.FAQ_NOT_FOUND));
        return new FAQResponseDTO(faq);
    }

    /**
     * FAQ 수정
     */
    @Transactional
    public FAQResponseDTO updateFAQ(Long faqId, FAQRequestDTO requestDTO) {
        FAQ faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new CustomException(ErrorCode.FAQ_NOT_FOUND));

        faq.update(requestDTO.getTitle(), requestDTO.getContent());
        return new FAQResponseDTO(faq);
    }

    /**
     * FAQ 삭제
     */
    @Transactional
    public void deleteFAQ(Long faqId) {
        FAQ faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new CustomException(ErrorCode.FAQ_NOT_FOUND));
        faqRepository.delete(faq);
    }
}

