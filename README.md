## 플러스 주차 개인과제 기반 코드입니다.

## 과제 설명 
1. Transactional에 대한 이해
createReservation에 Transaciton을 사용

2. 인가에 대한 이해
```
@Getter
public enum Role {
    USER("user"),
    ADMIN("admin");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public static Role of(String roleName) {
        for (Role role : values()) {
            if (role.getName().equals(roleName)) {
                return role;
            }
        }

        throw new IllegalArgumentException("해당하는 이름의 권한을 찾을 수 없습니다: " + roleName);
    }
}
```
Enum에 Role type에 역학을 처리 & WebConfig 부분에 

```
    private static final String ADMIN_ROLE_REQUIRED_PATH_PATTERN = "/admins/*";

```
Admin 권한이 필요한 interceptor 적용 

3. N + 1
```
    // TODO: 3. N+1 문제
    @Transactional(readOnly = true)
    public void getReservations() {
        List<Reservation> reservations = reservationRepository.findAllReservationsWithJoin();
```
```
    @Query("select r from Reservation r join fetch r.user u join fetch r.item i")
    List<Reservation> findAllReservationsWithJoin();
```
fetch join으로 해결 

4. DB 접근 최소화
```
    @Query("select u from User u where u.id in :userIds and u.status = :status")
    List<User> findUserIdsAndStatus(@Param("userIds") List<Long> userIds, @Param("status") String status);
```
Join으로 DB 접근 최소화 

5. 동적 쿼리 이해
QueryDSL 사용
```
    // TODO: 5. QueryDSL 검색 개선
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId) {
        QReservation reservation = QReservation.reservation;
        QItem item = QItem.item;
        QUser user = QUser.user;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        BooleanBuilder builder = new BooleanBuilder();

        if (userId != null) {
            builder.and(reservation.user.id.eq(userId));
        }

        if (itemId != null) {
            builder.and(reservation.item.id.eq(itemId));
        }

        return queryFactory
                .select(Projections.constructor(
                        ReservationResponseDto.class,
                        reservation.id,
                        reservation.status,
                        item.id,
                        user.id
                ))
                .from(reservation)
                .join(reservation.item, item)
                .join(reservation.user, user)
                .where(builder)
                .fetch();

    }
```

6. 필요한 부분만 갱신하기
```
@Entity
@Getter
@DynamicInsert // sql에서 null인 필드는 insert하지 않는다.
// TODO: 6. Dynamic Insert
public class Item {
```

7.  리팩토링
```
package com.example.demo.reservation.type;

import com.example.demo.reservation.entity.Reservation;
import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("예약 대기 중") {
        @Override
        public void validateTransition(ReservationStatus status) {
            if (status != APPROVED) {
                throw new IllegalArgumentException("예약 대기 중 상태는 승인 상태로만 변경 가능합니다.");
            }
        }
    },

    APPROVED("승인") {
        @Override
        public void validateTransition(ReservationStatus status) {
            throw new IllegalArgumentException("승인 상태는 취소 상태 또는 만료 상태로만 변경 가능");
        }
    },

    CANCELED("취소됨") {
        @Override
        public void validateTransition(ReservationStatus status) {
            if (status != EXPIRED) {
                throw new IllegalArgumentException("취소 상태는 만료 상태로만 변경 가능합니다.");
            }
        }
    },

    EXPIRED("만료됨") {
        @Override
        public void validateTransition(ReservationStatus status) {
            if (status != PENDING) {
                throw new IllegalArgumentException("만료 상태는 예약 대기 중 상태로만 변경 가능합니다.");
            }
        }
    };

    private String message;

    ReservationStatus(String message) {
        this.message = message;
    }

    public abstract void validateTransition(ReservationStatus status);
}
```
enum으로 해결 

8. test Code
```
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void getDefaultStatusIsNull() {
        User owner = new User();
        User manager = new User();

        Item item = Item.builder()
                .owner(owner)
                .manager(manager)
                .description("test")
                .status(null)  // 명시적으로 null 지정
                .build();

        Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            itemRepository.save(item);
            itemRepository.flush();  // 즉시 데이터베이스 동기화
        });
    }
}

@SpringBootTest
class UserServiceTest {


    @Autowired
    private UserService userService;

    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        userRequestDto = UserRequestDto.builder()
                .password("password123") // 예시 비밀번호
                .email("test@test.com") // 유효한 이메일 포맷으로 변경
                .role("user")
                .build();
    }

    @Test
    void signupWithEmail() {
        // 회원가입 수행
        UserResponseDto userResponseDto = userService.signupWithEmail(userRequestDto);

        // 이메일이 원본과 동일한지 확인
        assertEquals(userRequestDto.getEmail(), userResponseDto.getEmail());
        System.out.println("Email: " + userRequestDto.getEmail());

        assertEquals("test@test.com", userRequestDto.getEmail());

        // 비밀번호 암호화 검증
        // 디버깅용 출력
        System.out.println("Original Password: " + userRequestDto.getPassword()); // test
        System.out.println("Encoded Password: " + userResponseDto.getPassword()); // bcrypt 해시 값

        // 원래 비밀번호와 인코딩된 비밀번호가 매칭되는지 확인
        assertTrue(PasswordEncoder.matches("password123", userResponseDto.getPassword()));
    }
}
```
다음번에는 Mock을 사용해보려고 한다. 
