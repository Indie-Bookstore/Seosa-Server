package com.seosa.seosa.domain.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 사용 가이드 (postman 기준)

    // Presigned URL 발급 받기
    // 요청방식: get
    // 요청 url: http://서버도메인/s3/presign?filename=test.jpg (filename 파라미터는 업로드할 파일 이름)
    // 위와 같이 요청시 서버는 Presigned URL을 응답으로 반환

    // 파일 업로드
    // 요청방식: put
    // 요청 url: 위에서 받은 Presigned URL
    // Header 설정 (선택 사항): 파일 형식에 맞게 Content-Type 헤더를 설정 (ex: Content-Type: image/jpeg)
    // Body 설정: Body 탭에서 binary 옵션을 선택, 업로드할 파일 선택
    // 성공하면 HTTP 200이나 204 상태 코드가 돌아오며, 해당 파일이 S3 버킷에 업로드됨

    public String generatePresignedUrl(String filename) {
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10); // 10분 유효
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, filename)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}

