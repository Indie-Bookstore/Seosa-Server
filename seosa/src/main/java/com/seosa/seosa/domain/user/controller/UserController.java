package com.seosa.seosa.domain.user.controller;

import com.seosa.seosa.domain.user.dto.UpdatePasswordRequestDTO;
import com.seosa.seosa.domain.user.dto.UpdateProfileRequestDTO;
import com.seosa.seosa.domain.user.dto.UserResponseDTO;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.service.CheckService;
import com.seosa.seosa.domain.user.service.UserService;
import com.seosa.seosa.global.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated

@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "회원 관련 API")
public class UserController {

    private final UserService userService;
    private final CheckService checkService;

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
    public ResponseEntity<String> checkNickname(
            @RequestParam ("nickname")
            @NotBlank(message = "닉네임은 필수 입력 값입니다.")
            @Size(max = 10, message = "닉네임은 최대 10자 이내로 입력해주세요.")
            @Pattern(regexp = "^[가-힣0-9]+$", message = "닉네임은 한글과 숫자로만 구성되어야 합니다.")
            String nickname) {

        checkService.checkNickname(nickname);
        return ResponseEntity.ok("사용 가능한 닉네임입니다.");
    }


    @GetMapping("/checkEmail")
    @Operation(summary = "이메일 중복확인", description = "이메일 중복 여부를 확인하고 유효성을 검증합니다.")
    public ResponseEntity<String> checkEmail(
            @RequestParam("email")
            @NotBlank(message = "이메일은 필수 입력 값입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email) {

        checkService.checkEmail(email);
        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }
}
