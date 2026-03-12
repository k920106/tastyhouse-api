package com.tastyhouse.file.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * S3 저장소 자동 구성
 * file.storage.type=s3 일 때 활성화
 *
 * AWS 자격 증명은 환경 변수 또는 IAM 역할로 주입:
 *   AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_REGION
 *
 * spring-cloud-aws-s3가 S3Client 빈을 자동으로 등록하므로
 * application.yml의 spring.cloud.aws.s3.region 설정만으로 동작함.
 */
@Configuration
@ConditionalOnProperty(name = "file.storage.type", havingValue = "s3")
public class S3FileStorageConfig {
    // spring-cloud-aws-autoconfigure가 S3Operations, S3Client 빈을 자동 등록
    // 추가 커스터마이징이 필요하면 S3Client 빈을 여기서 재정의
}
