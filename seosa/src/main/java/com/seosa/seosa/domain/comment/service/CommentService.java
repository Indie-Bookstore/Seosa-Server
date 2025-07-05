package com.seosa.seosa.domain.comment.service;

import com.seosa.seosa.domain.bookmark.entity.Bookmark;
import com.seosa.seosa.domain.comment.dto.Request.CommentReqDto;
import com.seosa.seosa.domain.comment.dto.Response.CommentResDto;
import com.seosa.seosa.domain.comment.dto.Response.MyCommentListDto;
import com.seosa.seosa.domain.comment.entity.Comment;
import com.seosa.seosa.domain.comment.repository.CommentRepository;
import com.seosa.seosa.domain.post.dto.Response.PostCursorDto;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.post.repository.PostRepository;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import com.seosa.seosa.global.utils.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /* 댓글 작성 */
    public CommentResDto registerComment(User user, Long postId, CommentReqDto commentReqDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = CommentReqDto.toEntity(commentReqDto , post , user);
        commentRepository.save(comment);
        CommentResDto commentResDto = CommentResDto.to(comment , user , post);
        return  commentResDto;
    }

    /* 특정 댓글 단일 조회 */
    public CommentResDto getComment(User user, Long commentId) {

        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Post post = postRepository.findById(comment.getPost().getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));


        CommentResDto commentResDto = CommentResDto.to(comment , user , post);
        return commentResDto;
    }


    /* 특정 게시글의 댓글 목록 조회 */
    public List<CommentResDto> getCommentList(User user, Long postId) {
          Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
          List<Comment> comments = commentRepository.findByPostId(postId);

          List<CommentResDto> commentResDtos = comments.stream()
                  .map(comment -> CommentResDto.to(comment , user , post))
                  .collect(Collectors.toList());

          return  commentResDtos;
    }

    /* 특정 댓글 삭제 */
    public String deleteComment(User user, Long commentId) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if(!user.getUserId().equals(comment.getUser().getUserId())){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        commentRepository.delete(comment);

        return "해당 댓글이 삭제되었습니다.";
    }


    /* 사용자가 자신의 댓글 목록 조회 */
//    public List<CommentResDto> getMyCommentList(User user) {
//
//        List<Comment> commentList = commentRepository.findByUserId(user.getUserId());
//
//        List<CommentResDto> commentResDtos = commentList.stream()
//                .map(comment -> CommentResDto.to(comment, user, comment.getPost()))
//                .collect(Collectors.toList());
//
//        return commentResDtos;
//    }

    public MyCommentListDto getMyComments(Long userId, Integer cursorId, Pageable pageable) {

        // 커서 문자열 생성 (cursorId가 null일 경우 첫 페이지)
        String customCursor = null;
        if (cursorId != null) {
            Comment comment = commentRepository.findById(cursorId.longValue())
                    .orElseThrow(() -> new CustomException(ErrorCode.BOOKMARK_NOT_FOUND));

            customCursor = CursorUtils.generateCustomCursor(comment.getCreatedAt(), comment.getCommentId());

        }

        // 페이징된 북마크 조회
        MyCommentListDto pages = commentRepository.findMyCommentsWithCursor(userId, customCursor, pageable);

        return pages;
    }
}
