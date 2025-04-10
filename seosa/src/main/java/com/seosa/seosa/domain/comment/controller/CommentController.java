package com.seosa.seosa.domain.comment.controller;

import com.seosa.seosa.domain.comment.dto.Request.CommentReqDto;
import com.seosa.seosa.domain.comment.dto.Response.CommentResDto;
import com.seosa.seosa.domain.comment.service.CommentService;
import com.seosa.seosa.domain.post.dto.Response.PostCursorDto;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.global.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    /* 댓글 작성 */
    @PostMapping("/{postId}")
    @Operation(summary = "댓글 작성", description = "사용자가 게시글에 댓글을 작성합니다.")
    public ResponseEntity<CommentResDto> registerComment(@AuthUser User user , @PathVariable("postId") Long postId , @RequestBody CommentReqDto commentReqDto){
        CommentResDto commentResDto = commentService.registerComment(user , postId , commentReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentResDto);
    }

    /* 특정 댓글 단일 조회 */
    @GetMapping("/{commentId}")
    @Operation(summary = "댓글 단일 조회", description = "사용자가 게시글에 댓글을 단일 조회합니다.")
    public ResponseEntity<CommentResDto> getComment(@AuthUser User user  , @PathVariable("commentId") Long commentId ){
        CommentResDto commentSimpleResDto = commentService.getComment(user  , commentId);
        return ResponseEntity.ok(commentSimpleResDto);
    }

    /* 특정 게시글의 댓글 목록 조회 */
    @GetMapping("/post/{postId}")
    @Operation(summary = "특정 게시글의 댓글 목록 조회", description = "사용자가 게시글에 댓글 목록을 조회합니다.")
    public ResponseEntity<List<CommentResDto>> getCommentList(@AuthUser User user  , @PathVariable("postId") Long postId ){
        List<CommentResDto> commentList = commentService.getCommentList(user , postId);
        return ResponseEntity.ok(commentList);
    }

    /* 특정 댓글 삭제 */
    @DeleteMapping("/{commentId}")
    @Operation(summary = "특정 댓글을 삭제", description = "사용자가 자신이 쓴 특정 댓글을 삭제합니다.")
    public ResponseEntity<String> deleteComment(@AuthUser User user  , @PathVariable("commentId") Long commentId){
        String res = commentService.deleteComment(user , commentId);
        return ResponseEntity.ok(res);
    }

    /* 사용자가 자신의 댓글 목록 조회 */
//    @GetMapping
//    @Operation(summary = "사용자가 자신의 댓글 목록을 조회", description = "사용자가 자신의 댓글 목록을 최신순으로 조회합니다.")
//    public ResponseEntity<List<CommentResDto>> getMyCommentList(@AuthUser User user ){
//        List<CommentResDto> myCommentList = commentService.getMyCommentList(user);
//        return ResponseEntity.ok(myCommentList);
//    }

    /* 마이페이지 내 댓글 목록 조회 (9개씩)*/
    @GetMapping("/mypage")
    @Operation(summary = "내 댓글 목록 조회", description = "커서 기반으로 댓글을 9개씩 조회합니다.")
    public ResponseEntity<PostCursorDto> getMyComments(@AuthUser User user, @Parameter(description = "마지막 댓글 ID (다음 페이지 요청 시)")
    @RequestParam(required = false , name ="cursor") Integer cursorId, @PageableDefault(size = 9) Pageable pageable) {
        PostCursorDto response = commentService.getMyComments(user.getUserId(), cursorId, pageable);
        return ResponseEntity.ok(response);
    }
}
