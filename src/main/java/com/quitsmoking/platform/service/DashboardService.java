package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.DailySuccessAmountResponse;
import com.quitsmoking.platform.dto.PackageRevenueResponse;
import com.quitsmoking.platform.dto.TotalSuccessAmountResponse;
import com.quitsmoking.platform.entity.Package;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.repository.AccountRepository;
import com.quitsmoking.platform.repository.PackageRepository;
import com.quitsmoking.platform.repository.PaymentRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DashboardService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;

    public long getTotalActiveUser() {
        return purchasedPlanRepository.countActiveUser(PlanStatus.ACTIVE);
    }

    public long getTotalUserHasPurchasedPlan() {
        return purchasedPlanRepository.countUserHasPurchasedPlan();
    }


    public long getTotalUserCount() {
        return accountRepository.count();
    }

    public TotalSuccessAmountResponse getTotalSuccessAmount(LocalDate from, LocalDate to) {
        Double total = paymentRepository.calculateTotalSuccessAmount(
                from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return new TotalSuccessAmountResponse(total, "VND");
    }

    public DailySuccessAmountResponse getDailySuccessAmount(LocalDate from, LocalDate to) {
        List<Map<String, Object>> rawData = paymentRepository.getDailySuccessAmount(
                from.atStartOfDay(), to.plusDays(1).atStartOfDay());

        List<DailySuccessAmountResponse.DailyAmount> data = new ArrayList<>();
        double total = 0;

        for (Map<String, Object> item : rawData) {
            String date = item.get("date").toString();
            Double amount = ((Number) item.get("total")).doubleValue();
            total += amount;
            data.add(new DailySuccessAmountResponse.DailyAmount(date, amount));
        }

        return new DailySuccessAmountResponse("VND", total, data);
    }

    @Transactional
    public List<PackageRevenueResponse> getRevenueByPackage(LocalDate from, LocalDate to) {
        var start = from.atStartOfDay();
        var end = to.plusDays(1).atStartOfDay();

        List<Package> allPackages = packageRepository.findAll();
        List<PackageRevenueResponse> revenueData = paymentRepository.getRevenueByPackageId(start, end);

        List<PackageRevenueResponse> result = new ArrayList<>();

        for (Package pack : allPackages) {
            Optional<PackageRevenueResponse> match = revenueData.stream()
                    .filter(r -> r.getPackageId().equals(pack.getId()))
                    .findFirst();

            double amount = match.map(PackageRevenueResponse::getTotalAmount).orElse(0.0);

            result.add(new PackageRevenueResponse(
                    pack.getId(),
                    pack.getName(),
                    amount
            ));
        }

        return result;
    }
}
