package com.seosa.seosa.domain.bookmark.controller;

import com.seosa.seosa.domain.bookmark.dto.Response.BookmarkCursorDto;
import com.seosa.seosa.domain.bookmark.dto.Response.BookmarkResDto;
import com.seosa.seosa.domain.bookmark.service.BookmarkService;
import com.seosa.seosa.domain.post.dto.Response.PostCursorDto;
import com.seosa.seosa.domain.post.dto.Response.PostSimpleResDto;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.global.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bookmark API", description = "댓글 관련 API")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /* 북마크 하기 */
    @PostMapping("/{postId}/bookmark")
    @Operation(summary = "게시글에 북마크", description = "사용자가 특정 게시글을 북마크합니다.")
    public ResponseEntity<BookmarkResDto> doBookmark(@AuthUser User user , @PathVariable("postId") Long postId){
       BookmarkResDto bookmark = bookmarkService.doBookmark(user , postId);
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(bookmark);
    }

    /* 사용자가 자신의 북마크 목록을 조회 */
//    @GetMapping("/bookmark")
//    @Operation(summary = "자신의 북마크 목록 조회", description = "사용자가 자신의 북마크 목록을 조회합니다.")
//    public ResponseEntity<List<BookmarkResDto>> doBookmark(@AuthUser User user){
//        List<BookmarkResDto> bookmarkResDtos = bookmarkService.getBookmarks(user);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(bookmarkResDtos);
//    }

    /* 북마크 삭제 */
    @DeleteMapping("/{postId}/bookmark")
    @Operation(summary = "특정 북마크 취소", description = "사용자가 자신의 북마크를 취소합니다.")
    public ResponseEntity<String> deleteBookmark(@AuthUser User user , @PathVariable("postId")Long postId){
        String response = bookmarkService.deleteBookmark(user , postId);
        return ResponseEntity.ok(response);
    }

    /* 마이페이지 내 북마크 목록 조회 (9개씩)*/
    @GetMapping("/bookmark/mypage")
    @Operation(summary = "내 북마크 목록 조회", description = "커서 기반으로 북마크를 9개씩 조회합니다.")
    public ResponseEntity<PostCursorDto> getMyBookmarks(@AuthUser User user, @Parameter(description = "마지막 북마크 ID (다음 페이지 요청 시)")
    @RequestParam(required = false , name ="cursor") Integer cursorId, @PageableDefault(size = 9) Pageable pageable) {
        PostCursorDto response = bookmarkService.getMyBookmarks(user.getUserId(), cursorId, pageable);
        return ResponseEntity.ok(response);
    }
}
