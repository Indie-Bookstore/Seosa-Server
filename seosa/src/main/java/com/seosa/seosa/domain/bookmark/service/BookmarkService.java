package com.seosa.seosa.domain.bookmark.service;

import com.seosa.seosa.domain.bookmark.dto.Response.BookmarkCursorDto;
import com.seosa.seosa.domain.bookmark.dto.Response.BookmarkResDto;
import com.seosa.seosa.domain.bookmark.entity.Bookmark;
import com.seosa.seosa.domain.bookmark.repository.BookmarkRepository;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.post.repository.PostRepository;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import com.seosa.seosa.global.utils.CursorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private final PostRepository postRepository;
    private final BookmarkRepository bookmarkRepository;

    /* 북마크 생성 */
    public BookmarkResDto doBookmark(User user, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Bookmark bookmark = Bookmark.builder()
                .post(post)
                .user(user)
                .build();

        bookmarkRepository.save(bookmark);

        BookmarkResDto bookmarkResDto = BookmarkResDto.to(bookmark);
        return bookmarkResDto;
    }


    /* 북마크 목록 조회 */
    public List<BookmarkResDto> getBookmarks(User user) {

        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getUserId());

        List<BookmarkResDto> bookmarkResDtos = bookmarks.stream()
                .map(bookmark -> BookmarkResDto.to(bookmark))
                .collect(Collectors.toList());

        return bookmarkResDtos;
    }


    /* 북마크 삭제 */
    public String deleteBookmark(User user, Long bookmarkId) {
       Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
               .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));

       if(!bookmark.getUser().getUserId().equals(user.getUserId())){
           throw  new CustomException(ErrorCode.INVALID_ACCESS);
       }

       bookmarkRepository.delete(bookmark);

       return "해당 북마크가 삭제되었습니다.";
    }


    public BookmarkCursorDto getMyBookmarks(Long userId, Integer cursorId, Pageable pageable) {
        log.info("service , 지금 cursor Id : {}" , cursorId);
        log.info("service ,커서 북마크의 유저 ID: {}", userId);
        // 커서 문자열 생성 (cursorId가 null일 경우 첫 페이지)
        String customCursor = null;
        if (cursorId != null) {
            Bookmark bookmark = bookmarkRepository.findById(cursorId.longValue())
                    .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));
            log.info("여기까지 오나 1");
            customCursor = CursorUtils.generateCustomCursor(bookmark.getCreatedAt(), bookmark.getBookmarkId());
            log.info("여기까지 오나 2");
        }

        log.info("service customCursor 값 : {}" , customCursor);
        // 페이징된 북마크 조회
        Page<BookmarkResDto> page = bookmarkRepository.findMyBookmarksWithCursor(userId, customCursor, pageable);

        log.info("여기까지 오나3");

        List<BookmarkResDto> content = page.getContent();
        boolean hasNext = page.hasNext() ? true : false;
        int nextCursorId = content.isEmpty() ? 0 : content.get(content.size() - 1).bookmarkId().intValue();

        log.info("nextCursor id 값 : {}" , nextCursorId);

        return new BookmarkCursorDto(content, nextCursorId, hasNext);
    }
}
