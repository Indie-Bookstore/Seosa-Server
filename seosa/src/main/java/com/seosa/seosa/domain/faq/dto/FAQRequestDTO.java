package com.seosa.seosa.domain.faq.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FAQRequestDTO {

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;
}

