package com.seosa.seosa.domain.user.controller;

import com.seosa.seosa.domain.user.dto.UpdatePasswordRequestDTO;
import com.seosa.seosa.domain.user.dto.UpdateProfileRequestDTO;
import com.seosa.seosa.domain.user.dto.UserResponseDTO;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.service.CheckService;
import com.seosa.seosa.domain.user.service.UserService;
import com.seosa.seosa.domain.user.service.VerificationService;
import com.seosa.seosa.global.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "회원 관련 API")
public class UserController {

    private final UserService userService;
    private final CheckService checkService;
    private final VerificationService verificationService;

    @GetMapping
    @Operation(summary = "회원 정보 조회", description = "회원 정보를 조회합니다.")
    public ResponseEntity<UserResponseDTO> getUser(@AuthUser User user) {
        UserResponseDTO response = userService.getUserInfo(user);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile")
    @Operation(summary = "회원 프로필 수정", description = "회원 닉네임 또는 프로필 사진 정보를 수정합니다.")
    public ResponseEntity<String> updateUserProfile(
            @AuthUser User user,
            @RequestBody @Valid UpdateProfileRequestDTO request) {
        userService.updateUserProfile(user, request);
        return ResponseEntity.ok("프로필 수정이 완료되었습니다.");
    }

    @PatchMapping("/password")
    @Operation(summary = "회원 비밀번호 수정", description = "회원 비밀번호 정보를 수정합니다.")
    public ResponseEntity<String> updateUserPassword(
            @AuthUser User user,
            @RequestBody @Valid UpdatePasswordRequestDTO request) {
        userService.updateUserPassword(user, request);
        return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다.")
    public ResponseEntity<String> deleteUser(@AuthUser User user) {
        userService.deleteUser(user);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }


    @GetMapping("/checkNickname")
    @Operation(summary = "닉네임 중복확인", description = "닉네임 중복 여부를 확인하고 유효성을 검증합니다.")
    public ResponseEntity<String> checkNickname(@RequestParam ("nickname") String nickname) {

        checkService.checkNickname(nickname);
        return ResponseEntity.ok("사용 가능한 닉네임입니다.");
    }


    @GetMapping("/checkEmail")
    @Operation(summary = "이메일 중복확인", description = "이메일 중복 여부를 확인하고 유효성을 검증합니다.")
    public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {

        checkService.checkEmail(email);
        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }

    @GetMapping("/sendVerificationCode")
    @Operation(summary = "인증번호 전송", description = "회원 이메일로 인증번호를 전송합니다.")
    public ResponseEntity<String> sendVerificationCode(@AuthUser User user,
                                                       @RequestParam("email")
                                                       String email) {

        verificationService.sendVerificationCode(user, email);
        return ResponseEntity.ok("이메일로 인증번호를 전송했습니다.");
    }

    @GetMapping("/checkVerificationCode")
    @Operation(summary = "본인인증", description = "입력한 인증번호가 전송된 인증코드와 일치하는지 확인합니다.")
    public ResponseEntity<String> checkVerificationCode(@AuthUser User user,
                                                        @RequestParam("verificationCode")
                                                        String verificationCode) {

        verificationService.checkVerificationCode(user, verificationCode);
        return ResponseEntity.ok("인증번호가 확인되었습니다.");
    }
}
