package com.seosa.seosa.domain.user.service;

import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.global.annotation.AuthUser;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Validated
public class VerificationService {

    private final EmailService emailService;  // 이메일 전송 서비스
    private final StringRedisTemplate redisTemplate; // Redis 사용

    private static final long EXPIRATION_TIME = 5; // 인증번호 유효 시간 (5분)

    /**
     * 인증번호 생성 및 이메일 전송
     */
    public void sendVerificationCode(@AuthUser User user,
                                     @NotBlank(message = "이메일은 필수 입력 값입니다.")
                                     @Email(message = "이메일 형식이 올바르지 않습니다.")
                                     String email) {

        // 입력한 이메일이 회원정보와 일치하는지 확인
        String userEmail = user.getEmail();
        if (!userEmail.equals(email)) {
            throw new CustomException(ErrorCode.USER_EMAIL_MISMATCH);
        }

        // 인증번호 생성
        String verificationCode = generateVerificationCode();

        // Redis에 인증번호 저장 (5분 유효)
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(email, verificationCode, EXPIRATION_TIME, TimeUnit.MINUTES);

        // 이메일 전송
        emailService.sendEmail(email, "이메일 인증 코드", "인증번호: " + verificationCode);
    }

    /**
     * 입력한 인증번호가 저장된 인증번호와 일치하는지 확인
     */
    public void checkVerificationCode(@AuthUser User user, String verificationCode) {
        String email = user.getEmail(); // 회원 이메일

        // Redis에서 저장된 인증번호 조회
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String storedCode = ops.get(email);

        // 인증번호가 없거나 만료됨
        if (storedCode == null) {
            throw new CustomException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        // 인증번호 불일치
        if (!storedCode.equals(verificationCode)) {
            throw new CustomException(ErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        // 인증 성공 후 Redis에서 삭제
        redisTemplate.delete(email);
    }

    /**
     * 랜덤 6자리 인증번호 생성
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }
}
