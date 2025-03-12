package com.seosa.seosa.domain.post.service;


import com.seosa.seosa.domain.bookstore.entity.Bookstore;
import com.seosa.seosa.domain.bookstore.repository.BookstoreRepository;
import com.seosa.seosa.domain.content.entity.Content;
import com.seosa.seosa.domain.content.repository.ContentRepository;
import com.seosa.seosa.domain.post.dto.Request.BookstoreReqDto;
import com.seosa.seosa.domain.post.dto.Request.ContentReqDto;
import com.seosa.seosa.domain.post.dto.Request.PostReqDto;
import com.seosa.seosa.domain.post.dto.Request.ProductReqDto;
import com.seosa.seosa.domain.post.dto.Response.BookstoreResDto;
import com.seosa.seosa.domain.post.dto.Response.ContentResDto;
import com.seosa.seosa.domain.post.dto.Response.PostResDto;
import com.seosa.seosa.domain.post.dto.Response.ProductResDto;
import com.seosa.seosa.domain.post.entity.Post;
import com.seosa.seosa.domain.post.repository.PostRepository;
import com.seosa.seosa.domain.product.entity.Product;
import com.seosa.seosa.domain.product.repository.ProductRepository;
import com.seosa.seosa.domain.user.entity.User;
import com.seosa.seosa.domain.user.entity.UserRole;
import com.seosa.seosa.global.exception.CustomException;
import com.seosa.seosa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;



@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final BookstoreRepository bookstoreRepository;
    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final ProductRepository productRepository;

    /* 글 등록 */
    public PostResDto registerPost(User user, PostReqDto postReqDto) {
        validateAdminAccess(user);

        Bookstore bookstore = BookstoreReqDto.toEntity(postReqDto);
        bookstoreRepository.save(bookstore);

        Post post = PostReqDto.toEntity(postReqDto, bookstore, user);
        postRepository.save(post);

        List<Content> contentList = saveContents(postReqDto.getContentReqDtoList(), post);
        List<Product> productList = saveProducts(postReqDto.getProductReqDtoList(), bookstore);

        return convertToPostResDto(post, bookstore, contentList, productList);
    }

    /* 글 조회 */
    public PostResDto getPost(User user, Long postId) {
        Post post = findPostByIdAndUser(postId, user.getUserId());
        Bookstore bookstore = findBookstoreById(post.getBookstore().getBookstoreId());

        List<Content> contents = contentRepository.findByPostId(postId);
        List<Product> products = productRepository.findByBookstoreId(bookstore.getBookstoreId());

        return convertToPostResDto(post, bookstore, contents, products);
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
    private PostResDto convertToPostResDto(Post post, Bookstore bookstore, List<Content> contents, List<Product> products) {
        BookstoreResDto bookstoreResDto = BookstoreResDto.to(bookstore);
        List<ContentResDto> contentResDtos = contents.stream()
                .map(ContentResDto::to)
                .collect(Collectors.toList());
        List<ProductResDto> productResDtos = products.stream()
                .map(ProductResDto::to)
                .collect(Collectors.toList());

        return PostResDto.to(post, bookstoreResDto, contentResDtos, productResDtos);
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
                .map(contentReqDto -> ContentReqDto.toEntity(contentReqDto, post))
                .collect(Collectors.toList());
        return contentRepository.saveAll(contentList);
    }

    /*  Product 저장 */
    private List<Product> saveProducts(List<ProductReqDto> productReqDtoList, Bookstore bookstore) {
        List<Product> productList = productReqDtoList.stream()
                .map(productReqDto -> ProductReqDto.toEntity(productReqDto, bookstore))
                .collect(Collectors.toList());
        return productRepository.saveAll(productList);
    }
}