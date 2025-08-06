import http from 'k6/http';
import { check, sleep } from 'k6';

export default function () {
    // 공통 변수 설정
    const email = `user${__VU}_0@test.com`;
    const password = `passwordtest${__VU}`;

    const headers = { 'Content-Type': 'application/json' };

    // 로그인
    const loginPayload = JSON.stringify({ email, password });
    const loginRes = http.post('http://localhost:8080/local/login', loginPayload, { headers });

    check(loginRes, {
        '로그인 성공': (res) => res.status === 200,
        '토큰 존재': (res) => !!res.json('accessToken'),
    });

    const accessToken = loginRes.json('accessToken');
    const authHeaders = {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
    };

    // 댓글 작성 (예: 게시글 ID 1에 댓글 작성)
    const commentPayload = JSON.stringify({
        content: `테스트 댓글 by ${email}`
    });

    const writeRes = http.post('http://localhost:8080/comment/1', commentPayload, { headers: authHeaders });

    check(writeRes, {
        '댓글 작성 성공': (res) => res.status === 201,
    });

    // 댓글 조회
    const readRes = http.get('http://localhost:8080/comment/post/1', { headers: authHeaders });

    check(readRes, {
        '댓글 조회 성공': (res) => res.status === 200,
        '댓글 목록 존재': (res) => Array.isArray(res.json()) && res.json().length >= 1,
    });

    sleep(1); // 유휴 시간
}