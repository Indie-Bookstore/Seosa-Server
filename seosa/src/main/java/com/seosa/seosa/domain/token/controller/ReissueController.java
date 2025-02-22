package com.seosa.seosa.domain.token.controller;

import com.seosa.seosa.domain.jwt.JWTUtil;
import com.seosa.seosa.domain.token.entity.RefreshTokenEntity;
import com.seosa.seosa.domain.token.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@ResponseBody
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public ReissueController(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token (쿠키를 사용하고 있기 때문에 자동으로 가져옴)
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null) {
            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refreshToken")) {
            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenRepository.existsByRefreshToken(refreshToken);
        if (!isExist) {

            //response body
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        String userRole = jwtUtil.getUserRole(refreshToken);

        //make new JWT
        String newAccessToken = jwtUtil.createJwt("accessToken", userId, userRole, 30 * 60 * 1000L);
        String newRefreshToken = jwtUtil.createJwt("refreshToken", userId, userRole, 30* 24 * 60 * 60 * 1000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        addRefreshTokenEntity(userId, newRefreshToken, 86400000L);

        //response
        response.setHeader("accessToken", newAccessToken);
        response.addCookie(createCookie("refreshToken", newRefreshToken));

        /*
        Rotate 되기 이전의 토큰을 가지고 서버측으로 가도 인증이 되기 때문에
        서버측에서 발급했던 Refresh들을 기억한 뒤 블랙리스트 처리를 진행하는 로직을 작성해야 한다.
         -> Redis의 경우 TTL 설정을 통해 생명주기가 끝이난 토큰은 자동으로 삭제할 수 있음
         */

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addRefreshTokenEntity(Long userId, String refreshToken, Long refreshTokenExpiresAt) {

        Date date = new Date(System.currentTimeMillis() + refreshTokenExpiresAt);

        RefreshTokenEntity refreshEntity = new RefreshTokenEntity();
        refreshEntity.setUserId(userId);
        refreshEntity.setRefreshToken(refreshToken);
        refreshEntity.setRefreshTokenExpiresAt(date.toString());

        refreshTokenRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}