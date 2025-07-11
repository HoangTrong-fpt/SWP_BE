package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Id(Long userId);
    List<Booking> findByCoach_Id(Long coachId);
    List<Booking> findByCoachIdAndDateAndStartTimeAndStatusIn(Long coachId, LocalDate date, LocalTime startTime, List<String> statuses);

    // MỚI: Thêm phương thức này để tìm các lịch hẹn trong một ngày
    List<Booking> findByCoachIdAndDateAndStatusIn(Long coachId, LocalDate date, List<String> statuses);
}