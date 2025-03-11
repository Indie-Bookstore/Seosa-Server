package com.seosa.seosa.domain.faq.dto;

import com.seosa.seosa.domain.faq.entity.FAQ;
import lombok.Getter;

@Getter
public class FAQResponseDTO {

    private Long id;
    private String title;
    private String content;

    public FAQResponseDTO(FAQ faq) {
        this.id = faq.getFaqId();
        this.title = faq.getTitle();
        this.content = faq.getContent();
    }
}
