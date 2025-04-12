package com.seosa.seosa.domain.comment.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seosa.seosa.domain.bookmark.entity.Bookmark;
import com.seosa.seosa.domain.bookmark.entity.QBookmark;
import com.seosa.seosa.domain.comment.entity.Comment;
import com.seosa.seosa.domain.comment.entity.QComment;
import com.seosa.seosa.domain.post.dto.Response.PostCursorDto;
import com.seosa.seosa.domain.post.dto.Response.PostSimpleResDto;
import com.seosa.seosa.domain.post.entity.QPost;
import com.seosa.seosa.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
public class CommentRepositoryImpl implements CommentRepositoryCustom{


    private final JPAQueryFactory jpaQueryFactory;

    //지금은 중복처리를 못하고 있음
    @Override
    public PostCursorDto findMyCommentsWithCursor(Long userId, String customCursor, Pageable pageable) {
        QComment comment = QComment.comment;
        QUser user = QUser.user;
        QPost post = QPost.post;


        List<Comment> results = jpaQueryFactory
                .select(comment)
                .from(comment)
                .innerJoin(comment.user, user).fetchJoin()
                .innerJoin(comment.post, post).fetchJoin()
                .where(
                        user.userId.eq(userId),
                        customCursorCondition(customCursor)
                )
                .orderBy(comment.createdAt.desc(), comment.commentId.desc())
                .limit(pageable.getPageSize()+1)
                .fetch();



        // 다음 커서 계산
        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(pageable.getPageSize()); // 9번 인덱스 지우기
        }
        // DTO
        List<PostSimpleResDto> content = results.stream()
                .map(c -> PostSimpleResDto.from(c))
                .toList();

        int nextCursorId = results.isEmpty() ? 0 :
                results.get(results.size() - 1).getCommentId().intValue();



        return new PostCursorDto(content , nextCursorId , hasNext);
    }


    private BooleanExpression customCursorCondition(String customCursor) {

        if (customCursor == null) {
            return null;
        }

        QComment comment = QComment.comment;

        String datePart = customCursor.substring(0, 14);
        String idPart = customCursor.substring(14);

        LocalDateTime createdAtCursor = LocalDateTime.parse(datePart, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Long commentIdCursor = Long.parseLong(idPart);

        return comment.createdAt.lt(createdAtCursor) // createdAt보다 작거나 같고
                .or(comment.createdAt.eq(createdAtCursor)
                        .and(comment.commentId.lt(commentIdCursor))); // 댓글 id보다 작아야 함
    }

}
