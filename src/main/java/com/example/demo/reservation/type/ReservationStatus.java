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


