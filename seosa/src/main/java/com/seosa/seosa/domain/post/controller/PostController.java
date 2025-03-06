package com.seosa.seosa.domain.post.controller;

import com.seosa.seosa.domain.post.dto.Request.PostReqDto;
import com.seosa.seosa.domain.post.dto.Response.PostResDto;
import com.seosa.seosa.domain.post.service.PostService;
import com.seosa.seosa.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.seosa.seosa.global.annotation.AuthUser;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResDto> registerPost(@AuthUser User user , @RequestBody PostReqDto postReqDto){
        PostResDto postResDto = postService.registerPost(user, postReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postResDto);
    }

}
