# Tastyhouse API 프로젝트 분석 및 개선점 피드백

## 1. 프로젝트 구조 개요

```
tastyhouse-api/
├── core-module     # 엔티티, 리포지토리, 공통 서비스 (데이터 계층)
├── web-api         # 사용자용 REST API (port 8080)
├── admin-api       # 관리자용 REST API (port 8081)
├── external-api    # 외부 연동 (BBQ, 토스페이먼츠)
└── file-module     # 파일 저장소 추상화
```

- Spring Boot 3.2.4 / Java 21 / QueryDSL 5.0.0 / MySQL

---

## 2. 잘 되어 있는 점

| 항목                  | 설명                                                                             |
| --------------------- | -------------------------------------------------------------------------------- |
| 멀티 모듈 분리        | core, web-api, admin-api, external-api, file-module로 관심사가 명확히 분리됨     |
| Repository 패턴       | JpaRepository(단순 CRUD) + Custom Repository + QueryDSL Impl 3계층 구조가 일관적 |
| BaseEntity            | `createdAt`, `updatedAt` 감사(Audit) 필드가 공통화되어 있음                      |
| CommonResponse        | 통일된 API 응답 포맷 (success, message, data, pagination)                        |
| JWT 인증              | Access/Refresh Token 분리, Token Blacklist 로그아웃 처리                         |
| 토스 결제 연동        | 결제 승인/취소/환불 플로우가 체계적으로 구현됨                                   |
| Swagger 문서화        | OpenAPI 3.0 기반 API 문서화 적용                                                 |
| record 클래스 활용    | Request/Response DTO에 Java record 적극 활용                                     |
| 파일 저장소 전략 패턴 | `FileStorageStrategy` 인터페이스로 저장소 교체 가능                              |

---

## 3. 개선이 필요한 항목

### 3.5 [중요] 테스트 코드 부재

- 전체 프로젝트에서 테스트 파일이 **5개**뿐이며, 그중 2개는 빈 Application Test
- `PasswordEncodeTest`, `KeyGeneratorTest`, `RankServiceTest`만 존재
- 결제, 주문 등 핵심 비즈니스 로직에 대한 테스트가 전혀 없음
- **개선:**
  - 최소한 결제(`PaymentService`) 및 주문(`OrderService`) 관련 단위 테스트 추가
  - 컨트롤러 통합 테스트(`@WebMvcTest` 또는 `@SpringBootTest`) 추가
  - 테스트 전략 수립 (단위 > 통합 > E2E)

---

### 3.6 [보통] TokenBlacklist 메모리 이슈

**파일:** `TokenBlacklist.java`

```java
private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
```

- 인메모리 `ConcurrentHashMap`으로 구현되어 있어:
  - **서버 재시작 시 블랙리스트 초기화** (로그아웃한 토큰이 다시 유효해짐)
  - **다중 인스턴스 환경에서 동기화 불가** (Scale-out 시 문제)
  - 토큰이 많아지면 메모리 누수 가능 (`purgeExpired()`가 `add()` 시에만 호출됨)
- **개선:** Redis를 활용한 분산 Token Blacklist 구현

---

### 3.7 [보통] 입력값 검증(Validation) 부재

- Request DTO에 Bean Validation 어노테이션이 없음
- 컨트롤러에서 `@Valid` 어노테이션 미사용

```java
// 현재 - 검증 없이 바로 사용
public record OrderCreateRequest(
    Long placeId,
    List<OrderItemRequest> orderItems,
    ...
) {}
```

- **개선:**

```java
public record OrderCreateRequest(
    @NotNull(message = "매장 ID는 필수입니다") Long placeId,
    @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다") List<@Valid OrderItemRequest> orderItems,
    ...
) {}
```

---

### 3.10 [보통] 코드 품질

#### 3.10.1 Map<String, Object> 반환 타입

**파일:** `ReviewCoreService` > `getPlaceReviewStatistics()`

```java
Map<String, Object> statistics = reviewCoreService.getPlaceReviewStatistics(placeId);
// 타입 안전하지 않은 캐스팅
(Double) statistics.get("averageTasteRating")
```

- 타입 안전성이 없고, 키 이름이 변경되면 런타임 에러 발생
- **개선:** 전용 DTO 클래스로 대체

#### 3.10.2 사용되지 않는 코드

- `PlaceService.convertToPlacePhotoResponse()`는 private 메서드이나 실제 호출되는 곳이 없음
- `PlaceService.convertToPlaceReviewResponse()`도 동일
- **개선:** Dead code 제거

#### 3.10.3 WebClient를 매 요청마다 새로 생성

**파일:** `TossPaymentClient.java`

```java
webClientBuilder.build()  // 매 호출마다 새 WebClient 인스턴스 생성
    .post()
    ...
```

- **개선:** WebClient 인스턴스를 빈으로 등록하고 재사용

---

### 3.11 [경미] 기타 개선 사항

| 항목                    | 현재                                             | 개선안                                                  |
| ----------------------- | ------------------------------------------------ | ------------------------------------------------------- |
| Lombok 로거             | `JwtTokenProvider`에서 `LoggerFactory` 직접 사용 | `@Slf4j` 통일                                           |
| SQL 로깅                | `show_sql: true` + DEBUG 레벨                    | 운영 환경에서는 반드시 비활성화 필요, Profile 분리 필요 |
| `sql.init.mode: always` | 매 시작시 SQL 초기화 실행                        | 운영 환경에서 위험, Profile별 분기 필요                 |
| 파일 업로드 경로        | 절대 경로 하드코딩(`/Users/god/...`)             | 환경 변수로 외부화                                      |
| `.gitignore`            | 설정 파일이 커밋됨                               | 민감 정보가 포함된 yml 파일 관리 검토                   |

---

## 4. 우선순위별 개선 로드맵

### Phase 1 - 즉시 (보안)

1. 민감 정보 환경 변수 외부화 (DB 비밀번호, JWT 시크릿, 토스 키)
2. SecurityConfig에서 `permitAll()` → `authenticated()` 설정 변경
3. Profile 분리 (`application-dev.yml`, `application-prod.yml`)

### Phase 2 - 단기 (품질)

4. Request DTO에 Bean Validation 적용
5. 커스텀 예외 클래스 도입 및 GlobalExceptionHandler 보강
6. 코드 중복 제거 (`mapIssuerCodeToCardCompany`, `parseDateTime`, `getFirstImageUrl`)
7. 핵심 비즈니스 로직 단위 테스트 추가

### Phase 3 - 중기 (성능/구조)

8. N+1 쿼리 문제 해결 (Fetch Join 또는 DTO Projection)
9. TokenBlacklist Redis 전환
10. 서비스 계층 책임 분리 (OrderService 리팩토링)
11. 조회 메서드에 `@Transactional(readOnly = true)` 일괄 적용

### Phase 4 - 장기 (확장성)

12. JPA 연관관계 매핑 도입 검토
13. admin-api 기능 구현 또는 모듈 정리
14. API 응답 타입 통일
15. 통합 테스트 및 E2E 테스트 추가
