package com.seosa.seosa.domain.post.service;

import com.seosa.seosa.domain.bookmark.repository.BookmarkRepository;
import com.seosa.seosa.domain.bookstore.entity.Bookstore;
import com.seosa.seosa.domain.bookstore.repository.BookstoreRepository;
import com.seosa.seosa.domain.content.entity.Content;
import com.seosa.seosa.domain.content.entity.ContentType;
import com.seosa.seosa.domain.content.repository.ContentRepository;
import com.seosa.seosa.domain.post.dto.Request.BookstoreReqDto;
import com.seosa.seosa.domain.post.dto.Request.ContentReqDto;
import com.seosa.seosa.domain.post.dto.Request.PostReqDto;
import com.seosa.seosa.domain.post.dto.Request.ProductReqDto;
import com.seosa.seosa.domain.post.dto.Response.*;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.post.repository.PostRepository;
import com.seosa.seosa.domain.product.entity.Product;
import com.seosa.seosa.domain.product.repository.ProductRepository;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.entity.UserRole;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import com.seosa.seosa.global.utils.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;



@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final BookstoreRepository bookstoreRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final ProductRepository productRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region}")
    private String region;

    /* 글 등록 */
    public PostResDto registerPost(User user, PostReqDto postReqDto) {
        validateAdminAccess(user);

        Bookstore bookstore = BookstoreReqDto.toEntity(postReqDto);
        bookstoreRepository.save(bookstore);

        Post post = PostReqDto.toEntity(postReqDto, bookstore, user);
        postRepository.save(post);

        List<Content> contentList = saveContents(postReqDto.getContentReqDtoList(), post);
        List<Product> productList = saveProducts(postReqDto.getProductReqDtoList(), bookstore);

        return convertToPostResDto(user , post, bookstore, contentList, productList);
    }

    /* 글 조회 */
    public PostResDto getPost(User user, Long postId) {
        Post post = postRepository.findBypostId(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Bookstore bookstore = findBookstoreById(post.getBookstore().getBookstoreId());

        List<Content> contents = contentRepository.findByPostId(postId);
        List<Product> products = productRepository.findByBookstoreId(bookstore.getBookstoreId());

        return convertToPostResDto(user , post, bookstore, contents, products);
    }

    /* 글 삭제 */
    public String deletePost(User user, Long postId) {
        Post post = findPostByIdAndUser(postId, user.getUserId());

        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
        // 현재 글을 삭제하면 해당 글에 포함된 서점 정보 , 해당 서점의 물품을 다 삭제하게 구현함
        Bookstore bookstore = bookstoreRepository.findByBookstoreId(post.getBookstore().getBookstoreId())
                        .orElseThrow(() -> new CustomException(ErrorCode.BOOKSTORE_NOT_FOUND));
        List<Product> products = productRepository.findByBookstoreId(bookstore.getBookstoreId());

        productRepository.deleteAll(products);
        bookstoreRepository.delete(bookstore);

        postRepository.delete(post);
        return "해당 글이 삭제되었습니다.";
    }

    /* PostResDto 변환 */
    private PostResDto convertToPostResDto(User user , Post post, Bookstore bookstore, List<Content> contents, List<Product> products) {

        BookstoreResDto bookstoreResDto = BookstoreResDto.to(bookstore);
        List<ContentResDto> contentResDtos = contents.stream()
                .map(ContentResDto::to)
                .collect(Collectors.toList());
        List<ProductResDto> productResDtos = products.stream()
                .map(ProductResDto::to)
                .collect(Collectors.toList());
        boolean isBookmarked = bookmarkRepository.existsByUserIdAndPostId(user.getUserId() , post.getPostId());

        return PostResDto.to(post, bookstoreResDto, contentResDtos, productResDtos , isBookmarked);
    }

    /* 관리자 권한 확인 */
    private void validateAdminAccess(User user) {
        if (!(user.getUserRole().equals(UserRole.ADMIN) || user.getUserRole().equals(UserRole.EDITOR))) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
    }

    /* postId , userId로  Post 조회 */
    private Post findPostByIdAndUser(Long postId, Long userId) {
        return postRepository.findBypostIdAnduserId(postId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    /* Bookstore 조회 */
    private Bookstore findBookstoreById(Long bookstoreId) {
        return bookstoreRepository.findByBookstoreId(bookstoreId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOKSTORE_NOT_FOUND));
    }

    /* Content 저장 */
    private List<Content> saveContents(List<ContentReqDto> contentReqDtoList, Post post) {
        List<Content> contentList = contentReqDtoList.stream()
                .map(dto -> {
                    if (dto.getContentType() == ContentType.img_url) {
                        String s3Url = buildS3Url(dto.getContent()); // 객체 키 → 정적 URL
                        dto = new ContentReqDto(dto.getContentType(), s3Url, dto.getOrder_index());
                    }
                    return ContentReqDto.toEntity(dto, post);
                })
                .collect(Collectors.toList());

        return contentRepository.saveAll(contentList);
    }

    /* 객체 key 기반 정적 url 만들기 */
    private String buildS3Url(String objectKey){
        String imageUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + objectKey;
        return imageUrl;
    }

    /*  Product 저장 */
    private List<Product> saveProducts(List<ProductReqDto> productReqDtoList, Bookstore bookstore) {
        List<Product> productList = productReqDtoList.stream()
                .map(productReqDto -> {
                       String s3ImgUrl = buildS3Url( productReqDto.getProductImg());
                       return ProductReqDto.toEntity(productReqDto , s3ImgUrl, bookstore);
                })
                .collect(Collectors.toList());
        return productRepository.saveAll(productList);
    }

    /* 메인 페이지에서 게시물 5개씩 조회 */
    public PostCursorDto getMainPosts( Integer cursorId, Pageable pageable) {

        // 커서 문자열 생성 (cursorId가 null일 경우 첫 페이지)
        String customCursor = null;
        if (cursorId != null) {
            Post post = postRepository.findById(cursorId.longValue())
                    .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

            customCursor = CursorUtils.generateCustomCursor(post.getCreatedAt(), post.getPostId());

        }

        // 페이징된 게시물 조회
        Page<PostSimpleResDto> page = postRepository.findMainPostsWithCursor(customCursor, pageable);


        List<PostSimpleResDto> content = page.getContent();
        boolean hasNext = page.hasNext() ? true : false;
        int nextCursorId = content.isEmpty() ? 0 : content.get(content.size() - 1).postId().intValue();



        return new PostCursorDto(content, nextCursorId, hasNext);
    }

    /* 마이페이지에서 내가 쓴 글 조회 : 5개씩*/
    public PostCursorDto getMyPosts(Long userId, Integer cursorId, Pageable pageable) {

        // 커서 문자열 생성 (cursorId가 null일 경우 첫 페이지)
        String customCursor = null;
        if (cursorId != null) {
            Post post = postRepository.findById(cursorId.longValue())
                    .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

            customCursor = CursorUtils.generateCustomCursor(post.getCreatedAt(), post.getPostId());

        }

        // 페이징된 게시물 조회
        Page<PostSimpleResDto> page = postRepository.findMyPostsWithCursor(userId ,customCursor, pageable);


        List<PostSimpleResDto> content = page.getContent();
        boolean hasNext = page.hasNext() ? true : false;
        int nextCursorId = content.isEmpty() ? 0 : content.get(content.size() - 1).postId().intValue();



        return new PostCursorDto(content, nextCursorId, hasNext);

    }
}