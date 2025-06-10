package com.seosa.seosa.global.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("[handleCustomException] {} : {}", e.getErrorCode().name(), e.getErrorCode().getMessage());
        return ErrorResponse.fromException(e);
    }

    // content-type 잘못되었을 때
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        String unsupported = ex.getContentType() != null ? ex.getContentType().toString() : "알 수 없음";
        String supported = ex.getSupportedMediaTypes().stream()
                .map(MediaType::toString)
                .collect(Collectors.joining(", "));

        String message = String.format("지원하지 않는 Content-Type입니다: %s. 지원되는 형식: %s", unsupported, supported);
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)  // 415
                .body(Collections.singletonMap("error", message));
    }


    // JSON 형식 자체가 잘못되었을 때
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .badRequest()
                .body(Collections.singletonMap("error", "요청 형식이 올바르지 않습니다. JSON 구조를 확인해주세요."));
    }


    // parameter 누락시 에러처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        String message = String.format("필수 파라미터 '%s'가 누락되었습니다.", name);
        return ResponseEntity
                .badRequest()
                .body(Collections.singletonMap("error", message));
    }

    // @Valid 검증 실패 시 MethodArgumentNotValidException 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // 모든 필드 에러를 가져와서 저장
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("[handleValidationExceptions] 유효성 검사 실패: {}", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .code("VALIDATION_FAILED")
                        .message("유효성 검사 실패: " + errors)
                        .build());
    }

    // @Validated (RequestParam, PathVariable) 검증 실패 시 발생하는 예외 처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            errors.put(fieldName, violation.getMessage());
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .code("VALIDATION_FAILED")
                        .message("유효성 검사 실패: " + errors)
                        .build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("[handleMethodNotSupportedException] 지원되지 않는 요청 메서드: {}", ex.getMethod());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.METHOD_NOT_ALLOWED)
                        .code("METHOD_NOT_ALLOWED")
                        .message("지원되지 않는 HTTP 메서드입니다: " + ex.getMethod())
                        .build());
    }

}