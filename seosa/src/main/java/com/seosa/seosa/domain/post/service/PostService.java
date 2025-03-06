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

        if(!user.getUserRole().equals(UserRole.ADMIN)){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        Bookstore bookstore = BookstoreReqDto.toEntity(postReqDto);
        bookstoreRepository.save(bookstore);

        Post post = PostReqDto.toEntity(postReqDto , bookstore , user);
        postRepository.save(post);

        List<Content> contentList = postReqDto.getContentReqDtoList().stream()
                .map(contentReqDto -> ContentReqDto.toEntity(contentReqDto, post))
                .collect(Collectors.toList());
        contentRepository.saveAll(contentList);

        List<Product> productList = postReqDto.getProductReqDtoList().stream()
                .map(productReqDto -> ProductReqDto.toEntity(productReqDto,bookstore))
                .collect(Collectors.toList());

        productRepository.saveAll(productList);

        BookstoreResDto bookstoreResDto = BookstoreResDto.to(bookstore);
        List<ContentResDto> contentResDtos = contentList.stream()
                .map(content -> ContentResDto.to(content))
                .collect(Collectors.toList());
        List<ProductResDto> productResDtos = productList.stream()
                .map(product -> ProductResDto.to(product))
                .collect(Collectors.toList());

        PostResDto postResDto = PostResDto.to(post , bookstoreResDto , contentResDtos , productResDtos);

        return postResDto;
    }
}
