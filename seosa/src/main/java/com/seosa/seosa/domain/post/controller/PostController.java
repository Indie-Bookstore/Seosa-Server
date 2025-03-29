package com.seosa.seosa.domain.post.controller;

import com.seosa.seosa.domain.post.dto.Request.PostReqDto;
import com.seosa.seosa.domain.post.dto.Response.PostCursorDto;
import com.seosa.seosa.domain.post.dto.Response.PostResDto;
import com.seosa.seosa.domain.post.service.PostService;
import com.seosa.seosa.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.seosa.seosa.global.annotation.AuthUser;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@Tag(name = "Post API", description = "글 관련 API")
public class PostController {

    private final PostService postService;

    /*글 작성 api */
    @PostMapping
    @Operation(summary = "글 작성", description = "ADMIN 사용자가 새로운 글을 작성합니다.글 , 서점 , 서점 제품이 포함되어야 함")
    public ResponseEntity<PostResDto> registerPost(@AuthUser User user , @RequestBody PostReqDto postReqDto){
        PostResDto postResDto = postService.registerPost(user, postReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postResDto);
    }

    /* 글 조회 api */
    @GetMapping("/{postId}")
    @Operation(summary = "글 조회", description = "글을 조회합니다.")
    public ResponseEntity<PostResDto> getPost(@AuthUser User user , @PathVariable("postId") Long postId){
        PostResDto postResDto = postService.getPost(user , postId);
        return ResponseEntity.ok(postResDto);
    }

    /* 글 삭제*/
    @DeleteMapping("/{postId}")
    @Operation(summary = "글 삭제", description = "글을 삭제합니다.")
    public ResponseEntity<String> deletePost(@AuthUser User user , @PathVariable("postId") Long postId){
        String res = postService.deletePost(user , postId);
        return ResponseEntity.ok(res);
    }

    /* 메인 페이지 글 목록 조회 : 5개씩 */
    @GetMapping("/main")
    @Operation(summary = "메인 페이지 글 조회", description = "커서 기반으로 게시물을 5개씩 조회합니다.")
    public ResponseEntity<PostCursorDto> getMainPosts(@Parameter(description = "마지막 북마크 ID (다음 페이지 요청 시)")
    @RequestParam(required = false , name ="cursor") Integer cursorId, @PageableDefault(size = 5) Pageable pageable) {
        PostCursorDto response = postService.getMainPosts(cursorId, pageable);
        return ResponseEntity.ok(response);
    }

    /* 마이페이지에서 글 목록 조회 : 9개씩 */
    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 글 조회", description = "커서 기반으로 마이페이지 내 게시물을 5개씩 조회합니다.")
    public ResponseEntity<PostCursorDto> getMyPosts(@AuthUser User user ,@Parameter(description = "마지막 북마크 ID (다음 페이지 요청 시)")
                                                      @RequestParam(required = false , name ="cursor") Integer cursorId, @PageableDefault(size = 5) Pageable pageable) {
        PostCursorDto response = postService.getMyPosts(user.getUserId() ,cursorId, pageable);
        return ResponseEntity.ok(response);
    }

}
