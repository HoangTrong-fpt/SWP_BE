package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.dto.PackageRevenueResponse;
import com.quitsmoking.platform.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Tổng tiền giao dịch thành công
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE p.status = 'SUCCESS' AND p.completedAt BETWEEN :start AND :end")
    Double calculateTotalSuccessAmount(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    // Tổng tiền từng ngày (dạng thô để service xử lý)
    @Query("SELECT NEW map(FUNCTION('DATE', p.completedAt) AS date, COALESCE(SUM(p.amount), 0) AS total) " +
            "FROM Payment p " +
            "WHERE p.status = 'SUCCESS' AND p.completedAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE', p.completedAt) " +
            "ORDER BY FUNCTION('DATE', p.completedAt) ASC")
    List<Map<String, Object>> getDailySuccessAmount(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    // Thống kê doanh thu từng gói (trả về DTO luôn)
    @Query("""
        SELECT new com.quitsmoking.platform.dto.PackageRevenueResponse(
            pp.planPackage.id,
            pp.planPackage.name,
            SUM(p.amount)
        )
        FROM Payment p
        JOIN p.purchasedPlan pp
        WHERE p.status = 'SUCCESS'
          AND p.completedAt BETWEEN :start AND :end
        GROUP BY pp.planPackage.id, pp.planPackage.name
        ORDER BY pp.planPackage.id
    """)
    List<PackageRevenueResponse> getRevenueByPackageId(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
