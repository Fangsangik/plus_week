package com.example.demo.rentallog.repository;

import com.example.demo.rentallog.entity.RentalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalLogRepository extends JpaRepository<RentalLog, Long> {
}
