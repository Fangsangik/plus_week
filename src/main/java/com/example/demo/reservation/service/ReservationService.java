package com.example.demo.reservation.service;

import com.example.demo.item.entity.QItem;
import com.example.demo.reservation.dto.ReservationResponseDto;
import com.example.demo.item.entity.Item;
import com.example.demo.rentallog.entity.RentalLog;
import com.example.demo.reservation.entity.QReservation;
import com.example.demo.reservation.entity.Reservation;
import com.example.demo.rentallog.service.RentalLogService;
import com.example.demo.reservation.repository.ReservationRepository;
import com.example.demo.reservation.type.ReservationStatus;
import com.example.demo.user.entity.QUser;
import com.example.demo.user.entity.User;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.item.repository.ItemRepository;
import com.example.demo.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.demo.reservation.type.ReservationStatus.PENDING;


@Service
public class ReservationService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ReservationRepository reservationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalLogService rentalLogService;

    public ReservationService(ReservationRepository reservationRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              RentalLogService rentalLogService) {
        this.reservationRepository = reservationRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.rentalLogService = rentalLogService;
    }

    // TODO: 1. 트랜잭션 이해
    @Transactional
    public void createReservation(Long itemId, Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        // 쉽게 데이터를 생성하려면 아래 유효성검사 주석 처리
        List<Reservation> haveReservations = reservationRepository.findConflictingReservations(itemId, startAt, endAt);
        if (!haveReservations.isEmpty()) {
            throw new ReservationConflictException("해당 물건은 이미 그 시간에 예약이 있습니다.");
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));
        Reservation reservation = new Reservation(item, user, PENDING, startAt, endAt);
        Reservation savedReservation = reservationRepository.save(reservation);

        RentalLog rentalLog = new RentalLog(savedReservation, "로그 메세지", "CREATE");
        rentalLogService.save(rentalLog);
    }

    // TODO: 3. N+1 문제
    @Transactional(readOnly = true)
    public void getReservations() {
        List<Reservation> reservations = reservationRepository.findAllReservationsWithJoin();

//        return reservations.stream().map(reservation -> {
//            User user = reservation.getUser();
//            Item item = reservation.getItem();
//
//            return new ReservationResponseDto(
//                    reservation.getId(),
//                    user.getNickname(),
//                    item.getName(),
//                    reservation.getStartAt(),
//                    reservation.getEndAt()
//            );
//        }).toList();
    }

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


    // TODO: 7. 리팩토링
    @Transactional
    public void updateReservationStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()
                -> new IllegalArgumentException("해당 ID에 맞는 데이터가 존재하지 않습니다."));
        reservation.updateStatus(status);
    }
}
