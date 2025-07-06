
package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Id(Long userId);
    List<Booking> findByCoach_Id(Long coachId);
}
