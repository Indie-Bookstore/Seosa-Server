package com.seosa.seosa.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;


import com.seosa.seosa.domain.post.dto.Response.PostSimpleResDto;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.post.entity.QPost;
import com.seosa.seosa.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    /* 메인 페이지에서 글 목록 조회 */
    @Override
    public Page<PostSimpleResDto> findMainPostsWithCursor(String customCursor, Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> results = jpaQueryFactory
                .select(post)
                .from(post)
                .innerJoin(post.user, user).fetchJoin()
                .where(
                        customCursorCondition(customCursor)
                )
                .orderBy(post.createdAt.desc(), post.postId.desc())
                .limit(pageable.getPageSize()+1)
                .fetch();

        // 다음 커서 계산
        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(pageable.getPageSize()); // 5번 인덱스 지우기
        }
        // DTO
        List<PostSimpleResDto> content = results.stream()
                .map(p -> PostSimpleResDto.to(p))
                .toList();

        //  PageImpl 생성
        return new PageImpl<>(content, pageable, hasNext ? pageable.getPageSize() + 1 : content.size());
    }

    /* 메인 페이지에서 offset 기반 페이지네이션 */
    public Page<PostSimpleResDto> findAllPostsWithOffset(Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> results = jpaQueryFactory
                .select(post)
                .from(post)
                .innerJoin(post.user, user).fetchJoin()
                .orderBy(post.createdAt.desc(), post.postId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PostSimpleResDto> content = results.stream()
                .map(PostSimpleResDto::to)
                .toList();

        Long total = jpaQueryFactory
                .select(post.count())
                .from(post)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    /* 마이페이지에서 내 글 목록 조회 */
    public Page<PostSimpleResDto> findMyPostsWithCursor(Long userId ,String customCursor, Pageable pageable) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        List<Post> results = jpaQueryFactory
                .select(post)
                .from(post)
                .innerJoin(post.user, user).fetchJoin()
                .where(
                        user.userId.eq(userId),
                        customCursorCondition(customCursor)
                )
                .orderBy(post.createdAt.desc(), post.postId.desc())
                .limit(pageable.getPageSize()+1)
                .fetch();

        // 다음 커서 계산
        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(pageable.getPageSize()); // 5번 인덱스 지우기
        }
        // DTO
        List<PostSimpleResDto> content = results.stream()
                .map(p -> PostSimpleResDto.to(p))
                .toList();

        //  PageImpl 생성
        return new PageImpl<>(content, pageable, hasNext ? pageable.getPageSize() + 1 : content.size());
    }


    private BooleanExpression customCursorCondition(String customCursor) {

        if (customCursor == null) {
            return null;
        }

        QPost post = QPost.post;

        String datePart = customCursor.substring(0, 14);
        String idPart = customCursor.substring(14);

        LocalDateTime createdAtCursor = LocalDateTime.parse(datePart, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Long postIdCursor = Long.parseLong(idPart);

        return post.createdAt.lt(createdAtCursor) // createdAt보다 작거나 같고
                .or(post.createdAt.eq(createdAtCursor)
                        .and(post.postId.lt(postIdCursor))); // post id보다 작아야 함
    }
}
