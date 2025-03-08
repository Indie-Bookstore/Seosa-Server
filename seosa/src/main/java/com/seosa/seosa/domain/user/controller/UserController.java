package com.seosa.seosa.domain.user.controller;

import com.seosa.seosa.domain.user.dto.UpdatePasswordRequestDTO;
import com.seosa.seosa.domain.user.dto.UpdateProfileRequestDTO;
import com.seosa.seosa.domain.user.dto.UserResponseDTO;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.service.UserService;
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
}
