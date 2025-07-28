package com.quitsmoking.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.enums.AddictionLevel;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class TemplatePlanBuilder {

    @Autowired
    private ObjectMapper objectMapper;

    // DANH SÁCH 90 LỜI KHUYÊN - Thêm đủ 90 dòng
    private static final String[] DAILY_TIPS = {
            "Khi thèm thuốc, hãy uống một cốc nước lạnh.",
            "Hít thở sâu 10 lần khi cơn thèm xuất hiện.",
            "Tránh xa nơi từng hút thuốc quen thuộc.",
            "Tập thể dục nhẹ như đi bộ hoặc đạp xe.",
            "Nhắn tin cho người thân hoặc bạn bè khi thấy khó chịu.",
            "Thưởng cho bản thân một món ăn nhỏ khi vượt qua một ngày không hút.",
            "Sử dụng kẹo cao su không đường hoặc trái cây tươi thay thế khi buồn miệng.",
            "Tham gia nhóm hỗ trợ hoặc diễn đàn bỏ thuốc.",
            "Tìm hiểu về tác hại của thuốc lá qua sách báo, mạng.",
            "Đổi không gian làm việc sạch sẽ, loại bỏ gạt tàn, bật lửa.",
            "Tắm nước ấm thư giãn khi stress.",
            "Ngủ đủ giấc để kiểm soát cảm xúc.",
            "Viết nhật ký cảm xúc mỗi ngày.",
            "Tập yoga hoặc thiền 10 phút ngày hôm nay.",
            "Gọi điện cho bạn thân tâm sự lúc buồn.",
            "Xem phim yêu thích hoặc nghe nhạc mỗi tối.",
            "Học một kỹ năng mới hoặc đọc sách để bận rộn.",
            "Đặt mục tiêu nhỏ: 1 ngày, 1 tuần không hút.",
            "Chia sẻ thành tích lên mạng xã hội để có động lực.",
            "Đặt ảnh người thân làm hình nền điện thoại để nhắc nhở.",
            "Luôn mang theo kẹo bạc hà, nhai khi thèm thuốc.",
            "Rửa mặt bằng nước lạnh khi thèm thuốc.",
            "Chơi game hoặc giải đố khi rảnh rỗi.",
            "Uống nhiều nước mỗi ngày.",
            "Đặt báo thức nhắc nhở mục tiêu bỏ thuốc mỗi sáng.",
            "Nói không khi được mời thuốc.",
            "Thường xuyên kiểm tra lại danh sách lợi ích khi bỏ thuốc.",
            "Tạo playlist nhạc giúp thư giãn.",
            "Ăn trái cây, uống sinh tố thay cho thuốc lá."
    };

    private List<String> dailyTips; // Coach chọn, có thể null nếu random

    public QuitPlan build(Account account, PurchasedPlan purchasedPlan, InitialCondition ic, List<String> customTips) {
        QuitPlan plan = new QuitPlan();

        plan.setAccount(account);
        plan.setPurchasedPlan(purchasedPlan);
        plan.setStartDate(purchasedPlan.getActivationDate());
        plan.setMethod(MethodType.PLAN_SAMPLE);

        int expectedDays = Optional.ofNullable(purchasedPlan.getPlanPackage().getDuration()).orElse(30);
        plan.setTargetQuitDate(plan.getStartDate().plusDays(expectedDays - 1));

        plan.setPlanDetail(
                generateTemplatePlanDetail(
                        Math.max(1, ic.getCigarettesPerDay()),
                        expectedDays,
                        purchasedPlan.getPlanPackage().getDescription(),
                        ic.getAddictionLevel() != null ? ic.getAddictionLevel() : AddictionLevel.MODERATE, // fallback
                        customTips
                )
        );

        plan.setStatus(PlanStatus.ACTIVE);
        plan.setCreatedAt(LocalDate.now());

        return plan;
    }

    private String generateTemplatePlanDetail(int startCigarettesPerDay, int totalDays, String noteDescription, AddictionLevel addictionLevel, List<String> customTips) {
        List<Map<String, Object>> planDetails = new ArrayList<>();

        double[] floatPlan = new double[totalDays];
        int[] roundedPlan = new int[totalDays];

        int target = 0;

        // Xác định tỷ lệ giảm chậm tùy theo mức độ nghiện
        double slowPhaseRatio = switch (addictionLevel) {
            case LIGHT -> 0.0;
            case MODERATE -> (totalDays >= 21 ? 0.3 : 0.2);
            case SEVERE -> (totalDays >= 21 ? 0.5 : 0.3);
        };

        int slowDays = (int) Math.round(totalDays * slowPhaseRatio);
        int fastDays = totalDays - slowDays;

        double slowDrop = 0.3;
        double current = startCigarettesPerDay;

        // Giai đoạn chậm: giảm ít mỗi ngày
        for (int i = 0; i < slowDays; i++) {
            current = Math.max(target, current - slowDrop);
            floatPlan[i] = current;
        }

        // Giai đoạn nhanh: chia đều phần còn lại để giảm
        double fastStep = (current - target) / Math.max(1, fastDays);
        for (int i = slowDays; i < totalDays; i++) {
            current = Math.max(target, current - fastStep);
            floatPlan[i] = current;
        }

        // Làm tròn xuống từng ngày & đảm bảo giảm dần không tăng lại
        int lastValue = startCigarettesPerDay + 1;
        for (int i = 0; i < totalDays; i++) {
            int value = (int) Math.floor(floatPlan[i]);
            value = Math.min(value, lastValue);
            value = Math.max(0, value);
            roundedPlan[i] = value;
            lastValue = value;
        }

        // Ép ngày cuối là 0
        roundedPlan[totalDays - 1] = 0;

        // Random tips cho từng ngày
        Random random = new Random();
        for (int day = 1; day <= totalDays; day++) {
            Map<String, Object> dayTask = new HashMap<>();
            dayTask.put("day", day);
            dayTask.put("cigarettes", roundedPlan[day - 1]);
            String tip;
            if (customTips != null && customTips.size() >= day) {
                tip = customTips.get(day - 1);
            } else {
                tip = DAILY_TIPS[random.nextInt(DAILY_TIPS.length)];
            }
            dayTask.put("note", tip);
            planDetails.add(dayTask);
        }

        try {
            return objectMapper.writeValueAsString(planDetails);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi sinh plan detail", e);
        }
    }

    public List<String> getDailyTips() {
        return Arrays.asList(DAILY_TIPS);
    }
}
