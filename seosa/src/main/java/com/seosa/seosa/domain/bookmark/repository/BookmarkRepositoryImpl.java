package com.seosa.seosa.domain.bookmark.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seosa.seosa.domain.bookmark.dto.Response.BookmarkResDto;
import com.seosa.seosa.domain.bookmark.entity.Bookmark;
import com.seosa.seosa.domain.bookmark.entity.QBookmark;
import com.seosa.seosa.domain.post.entity.QPost;
import com.seosa.seosa.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<BookmarkResDto> findMyBookmarksWithCursor(Long userId, String customCursor, Pageable pageable) {
        QBookmark bookmark = QBookmark.bookmark;

        log.info(">> 북마크 커서 기반 조회 실행");
        log.info("쿼리 실행 직전 - 커서: {}", customCursor);
        // limit + 1 개 조회 ( 다음 페이지가 있는 지 확인하기 위해서)
        List<Bookmark> results = jpaQueryFactory
                .selectFrom(bookmark)
                .where(
                        bookmark.user.userId.eq(userId),
                        customCursorCondition(customCursor)
                )
                .orderBy(bookmark.createdAt.desc(), bookmark.bookmarkId.desc())
                .limit(pageable.getPageSize() + 1) // 다음 커서 존재 여부 확인용
                .fetch();
        log.info("쿼리 실행 완료 - 결과 개수: {}", results.size());

        log.info(">> 북마크 커서 기반 조회 실행 2");

        // 다음 커서 계산
        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(pageable.getPageSize()); // 9번 인덱스 지우기
        }

        // DTO
        List<BookmarkResDto> content = results.stream()
                .map(b -> BookmarkResDto.to(b))
                .toList();

        //  PageImpl 생성
        return new PageImpl<>(content, pageable, hasNext ? pageable.getPageSize() + 1 : content.size());
    }


    private BooleanExpression customCursorCondition(String customCursor) {
        if (customCursor == null) {
            return null;
        }

        QBookmark bookmark = QBookmark.bookmark;

        StringTemplate dateFormatted = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                bookmark.createdAt,
                ConstantImpl.create("%Y%m%d%H%i%s")
        );

        return StringExpressions.lpad(dateFormatted, 20, '0')
                .concat(StringExpressions.lpad(bookmark.bookmarkId.stringValue(), 10, '0'))
                .gt(customCursor); // 커서 이후의 데이터만 조회
    }
}
