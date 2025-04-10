package com.seosa.seosa.domain.bookmark.repository;

import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seosa.seosa.domain.bookmark.dto.Response.BookmarkResDto;
import com.seosa.seosa.domain.bookmark.entity.Bookmark;
import com.seosa.seosa.domain.bookmark.entity.QBookmark;
import com.seosa.seosa.domain.post.dto.Response.PostCursorDto;
import com.seosa.seosa.domain.post.dto.Response.PostSimpleResDto;
import com.seosa.seosa.domain.post.entity.QPost;
import com.seosa.seosa.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PostCursorDto findMyBookmarksWithCursor(Long userId, String customCursor, Pageable pageable) {
        QBookmark bookmark = QBookmark.bookmark;
        QUser user = QUser.user;
        QPost post = QPost.post;


        List<Bookmark> results = jpaQueryFactory
                .select(bookmark)
                .from(bookmark)
                .innerJoin(bookmark.user, user).fetchJoin()
                .innerJoin(bookmark.post, post).fetchJoin()
                .where(
                        user.userId.eq(userId),
                        customCursorCondition(customCursor)
                )
                .orderBy(bookmark.createdAt.desc(), bookmark.bookmarkId.desc())
                .limit(pageable.getPageSize()+1)
                .fetch();



        // 다음 커서 계산
        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(pageable.getPageSize()); // 9번 인덱스 지우기
        }
        // DTO
        List<PostSimpleResDto> content = results.stream()
                .map(b -> PostSimpleResDto.from(b))
                .toList();

        int nextCursorId = results.isEmpty() ? 0 :
                results.get(results.size() - 1).getBookmarkId().intValue();



        return new PostCursorDto(content , nextCursorId , hasNext);
    }


    private BooleanExpression customCursorCondition(String customCursor) {

        if (customCursor == null) {
            return null;
        }

        QBookmark bookmark = QBookmark.bookmark;

        String datePart = customCursor.substring(0, 14);
        String idPart = customCursor.substring(14);

        LocalDateTime createdAtCursor = LocalDateTime.parse(datePart, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Long bookmarkIdCursor = Long.parseLong(idPart);

        return bookmark.createdAt.lt(createdAtCursor) // createdAt보다 작거나 같고
                .or(bookmark.createdAt.eq(createdAtCursor)
                        .and(bookmark.bookmarkId.lt(bookmarkIdCursor))); // 북마크 id보다 작아야 함
    }

}
