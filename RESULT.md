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

| 항목 | 설명 |
|------|------|
| 멀티 모듈 분리 | core, web-api, admin-api, external-api, file-module로 관심사가 명확히 분리됨 |
| Repository 패턴 | JpaRepository(단순 CRUD) + Custom Repository + QueryDSL Impl 3계층 구조가 일관적 |
| BaseEntity | `createdAt`, `updatedAt` 감사(Audit) 필드가 공통화되어 있음 |
| CommonResponse | 통일된 API 응답 포맷 (success, message, data, pagination) |
| JWT 인증 | Access/Refresh Token 분리, Token Blacklist 로그아웃 처리 |
| 토스 결제 연동 | 결제 승인/취소/환불 플로우가 체계적으로 구현됨 |
| Swagger 문서화 | OpenAPI 3.0 기반 API 문서화 적용 |
| record 클래스 활용 | Request/Response DTO에 Java record 적극 활용 |
| 파일 저장소 전략 패턴 | `FileStorageStrategy` 인터페이스로 저장소 교체 가능 |

---

## 3. 개선이 필요한 항목

### 3.1 [심각] 보안 이슈

#### 3.1.1 설정 파일에 민감 정보 하드코딩

**파일:** `application-core.yml`, `web-api/application.yml`

```yaml
# application-core.yml
password: "!Tastyhouse!Application!1234!"

# web-api/application.yml
jwt:
  secret: Z1JUbfzFqiQ9RSX1Bh8s0B5Jsugvk3CV6YpD3RcLa/zqGxBdghHApgQ/0uct6Nw7DQh6kTnMgSdZRrnrXg1uCw==
```

- DB 비밀번호, JWT 시크릿 키가 소스 코드에 평문으로 노출됨
- 토스 시크릿 키는 `${TOSS_SECRET_KEY:...}` 형태이나 기본값에 실제 테스트 키가 포함됨
- **개선:** 모든 민감 정보를 환경 변수 또는 Vault/AWS Secrets Manager 등으로 외부화 필요

#### 3.1.2 모든 엔드포인트가 인증 없이 접근 가능

**파일:** `SecurityConfig.java:40`

```java
.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
```

- `anyRequest().permitAll()`로 설정되어 있어 JWT 필터가 존재해도 **모든 API가 비인증 상태로 접근 가능**
- **개선:** 공개 API(로그인, 회원가입 등)만 `permitAll()`, 나머지는 `authenticated()` 설정 필요

#### 3.1.3 JwtAuthenticationFilter에서 토큰 없는 요청 차단 불가

**파일:** `JwtAuthenticationFilter.java:40-55`

- JWT가 없는 요청은 그냥 통과시키지만, SecurityConfig에서 `permitAll()`이므로 인증 체계가 사실상 무력화됨
- 유효하지 않은 토큰이 있을 때만 401을 반환하는 구조

---

### 3.2 [중요] 아키텍처 개선

#### 3.2.1 엔티티 간 연관관계가 ID 참조(Long)로만 되어 있음

**파일:** `Order.java`

```java
@Column(name = "member_id", nullable = false)
private Long memberId;  // Member 엔티티 참조 대신 ID만 보유

@Column(name = "place_id", nullable = false)
private Long placeId;   // Place 엔티티 참조 대신 ID만 보유
```

- JPA의 연관관계 매핑(`@ManyToOne`, `@OneToMany`)을 전혀 사용하지 않음
- 이로 인해 서비스 계층에서 **N+1 문제가 빈번하게 발생**

**예시 - OrderService.java:263-278 (getOrderList):**
```java
// 주문 목록의 각 항목마다 개별 쿼리 실행
page.getContent().stream().map(order -> {
    Place place = placeJpaRepository.findById(order.getPlaceId()).orElse(null);     // N+1
    List<OrderItem> items = orderItemJpaRepository.findByOrderId(order.getId());     // N+1
    Payment payment = paymentJpaRepository.findByOrderId(order.getId()).orElse(null); // N+1
    ...
});
```

- **개선 방향:**
  - 단기: QueryDSL에서 `JOIN FETCH` 또는 `DTO Projection`으로 한 번에 조회
  - 장기: JPA 연관관계 매핑 도입 검토 (Lazy Loading + Fetch Join 전략)

#### 3.2.2 서비스 계층의 과도한 의존성

**파일:** `OrderService.java`

```java
public class OrderService {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderItemOptionJpaRepository orderItemOptionJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductImageJpaRepository productImageJpaRepository;
    private final ProductOptionGroupJpaRepository productOptionGroupJpaRepository;
    private final ProductOptionJpaRepository productOptionJpaRepository;
    private final PlaceJpaRepository placeJpaRepository;
    private final MemberCouponJpaRepository memberCouponJpaRepository;
    private final CouponJpaRepository couponJpaRepository;
    private final MemberPointJpaRepository memberPointJpaRepository;
    private final MemberPointHistoryJpaRepository memberPointHistoryJpaRepository;
    private final MemberService memberService;
    // 14개의 의존성!
}
```

- 단일 서비스에 14개의 의존성이 있어 **단일 책임 원칙(SRP) 위반**
- **개선:**
  - 주문 생성 / 주문 조회 / 쿠폰 적용 / 포인트 처리 등 별도 서비스로 분리
  - 또는 `CoreService` 계층을 활용하여 도메인별 위임

#### 3.2.3 core-module에 비즈니스 로직과 데이터 접근이 혼재

- `core-module`에 `CoreService`와 `Repository`가 함께 있어, 두 API 모듈(web-api, admin-api)이 공유하기 좋으나, CoreService의 책임 범위가 모호한 경우가 있음
- `PlaceCoreService`에 `BestPlacePageResult` 같은 inner class가 있어 web-api의 관심사가 core로 누출됨
- **개선:** CoreService의 반환 타입을 `Page<Entity>` 또는 `Page<DTO>`로 통일하고, 페이지 래핑은 각 API 모듈에서 수행

---

### 3.3 [중요] 코드 중복

#### 3.3.1 `mapIssuerCodeToCardCompany()` 메서드 중복

- `PaymentService.java:527-557`와 `TossPaymentClient.java:222-252`에 **완전히 동일한 코드**가 존재
- **개선:** `external-api` 모듈의 유틸리티 클래스로 통합하거나, `TossPaymentClient`의 메서드를 public으로 노출하여 재사용

#### 3.3.2 `parseDateTime()` 메서드 중복

- `PaymentService.java:511-525`와 `TossPaymentClient.java:209-220`에 동일한 날짜 파싱 로직 존재
- **개선:** 공통 유틸 클래스로 추출

#### 3.3.3 `getFirstImageUrl()` 메서드 중복

- `PlaceService.java:397-403`와 `ReviewService.java:330-336`에 동일 로직 존재
- **개선:** `ProductCoreService`에 공통 메서드로 이동

---

### 3.4 [중요] 예외 처리 개선

#### 3.4.1 커스텀 예외 클래스 부족

- 현재 `NotFoundException`과 `IllegalArgumentException`, `IllegalStateException`만 사용
- 비즈니스 예외를 Java 표준 예외로 처리하면 예외 원인을 구분하기 어려움

```java
// 현재 - 모두 IllegalArgumentException
throw new IllegalArgumentException("주문을 찾을 수 없습니다.");
throw new IllegalArgumentException("본인의 주문만 결제할 수 있습니다."); // 권한 문제인데 같은 예외
throw new IllegalStateException("결제할 수 없는 주문 상태입니다.");
```

- **개선:** 도메인별 커스텀 예외 도입
  - `AccessDeniedException` - 권한 없는 접근
  - `BusinessException` - 비즈니스 규칙 위반
  - `EntityNotFoundException` - 엔티티 미존재
  - 각 예외에 에러 코드(enum) 부여하여 클라이언트에서 분기 처리 가능하도록

#### 3.4.2 GlobalExceptionHandler 보강 필요

- `MethodArgumentNotValidException` (Bean Validation), `HttpMessageNotReadableException` (잘못된 JSON), `MissingServletRequestParameterException` 등의 핸들러 미존재
- **개선:** Spring MVC 표준 예외에 대한 핸들러 추가

---

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

### 3.8 [보통] 트랜잭션 관리 개선

#### 3.8.1 조회 메서드에 `@Transactional(readOnly = true)` 누락

- `PlaceService`, `ReviewService`, `BannerService` 등 대부분의 조회 메서드에 `@Transactional(readOnly = true)`가 없음
- readOnly 트랜잭션은 Hibernate 더티 체킹을 비활성화하여 성능 향상에 기여

#### 3.8.2 긴 트랜잭션 주의

- `OrderService.createOrder()`는 하나의 트랜잭션 안에서 주문 생성 + 쿠폰 사용 + 포인트 차감을 모두 처리
- 외부 API 호출이 포함되지 않아 현재는 괜찮으나, 향후 확장 시 트랜잭션 분리 고려 필요

---

### 3.9 [보통] API 설계 개선

#### 3.9.1 admin-api 모듈이 사실상 비어있음

- `AdminApiApplication.java` 1개만 존재
- **개선:** 관리자 기능을 admin-api에 구현하거나, 아직 필요 없다면 모듈 제거하여 불필요한 복잡성 해소

#### 3.9.2 PolicyAdminApiController가 web-api에 위치

- `web-api/policy/PolicyAdminApiController.java`는 관리자용 API이므로 `admin-api`에 위치해야 함

#### 3.9.3 일관되지 않은 응답 타입

- 일부 API는 `CommonResponse<T>`로 감싸고, 일부는 `ResponseEntity<T>`를 직접 반환
- 예: `AuthController`는 `ResponseEntity<JwtResponse>`, 다른 컨트롤러는 `CommonResponse<T>` 사용
- **개선:** 전체 API에 `CommonResponse<T>` 통일 적용

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

| 항목 | 현재 | 개선안 |
|------|------|--------|
| Lombok 로거 | `JwtTokenProvider`에서 `LoggerFactory` 직접 사용 | `@Slf4j` 통일 |
| SQL 로깅 | `show_sql: true` + DEBUG 레벨 | 운영 환경에서는 반드시 비활성화 필요, Profile 분리 필요 |
| `sql.init.mode: always` | 매 시작시 SQL 초기화 실행 | 운영 환경에서 위험, Profile별 분기 필요 |
| 파일 업로드 경로 | 절대 경로 하드코딩(`/Users/god/...`) | 환경 변수로 외부화 |
| `.gitignore` | 설정 파일이 커밋됨 | 민감 정보가 포함된 yml 파일 관리 검토 |

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
