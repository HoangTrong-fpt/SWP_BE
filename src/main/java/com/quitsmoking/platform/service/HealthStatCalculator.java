package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.HealthStatResponse;

public class HealthStatCalculator {

    /**
     * Tính toán các chỉ số sức khỏe, động viên/cảnh báo cá nhân hóa mỗi ngày
     */
    public static HealthStatResponse calculate(
            int cigarettesToday,
            int targetCigarettes,
            int baselineCigarettes,
            int pricePerCig
    ) {
        HealthStatResponse resp = new HealthStatResponse();
        resp.setCigarettesToday(cigarettesToday);
        resp.setTargetCigarettes(targetCigarettes);

        // 1. Nicotin ước lượng (giả định mỗi điếu ~ 1mg)
        resp.setNicotineEstimate(cigarettesToday * 1.0);

        // 2. CO máu - trạng thái
        if (cigarettesToday == 0) {
            resp.setCoStatus("Giảm mạnh");
        } else if (cigarettesToday <= targetCigarettes) {
            resp.setCoStatus("Cải thiện");
        } else if (cigarettesToday <= baselineCigarettes) {
            resp.setCoStatus("Ổn định");
        } else {
            resp.setCoStatus("Tăng cao");
        }

        // 3. Tiết kiệm hôm nay (cho phép âm khi vượt mục tiêu)
        int saved = (targetCigarettes - cigarettesToday) * pricePerCig;
        resp.setMoneySavedToday(saved);

        // 4. Chức năng phổi
        if (cigarettesToday == 0) {
            resp.setLungStatus("Cải thiện mạnh");
        } else if (cigarettesToday <= targetCigarettes) {
            resp.setLungStatus("Cải thiện");
        } else if (cigarettesToday <= baselineCigarettes) {
            resp.setLungStatus("Ổn định");
        } else {
            resp.setLungStatus("Xấu đi");
        }

        // 5. Vị giác, khứu giác
        if (cigarettesToday == 0) {
            resp.setTasteStatus("Cải thiện rõ");
        } else if (cigarettesToday <= targetCigarettes) {
            resp.setTasteStatus("Cải thiện");
        } else if (cigarettesToday <= baselineCigarettes) {
            resp.setTasteStatus("Bình thường");
        } else {
            resp.setTasteStatus("Giảm");
        }

        // 6. Đạt mục tiêu?
        resp.setTargetAchieved(cigarettesToday <= targetCigarettes);

        // 7. Nhịp tim động (giả lập hợp lý)
        int baseHeartRate = 70;
        int maxHeartRate = 90;
        int minHeartRate = 62;
        int heartRate;
        double ratio = baselineCigarettes == 0 ? 0 : (double) cigarettesToday / baselineCigarettes;

        if (cigarettesToday == 0) {
            heartRate = minHeartRate;
        } else if (ratio <= 1.0) {
            heartRate = baseHeartRate - (int) ((1.0 - ratio) * (baseHeartRate - minHeartRate));
        } else {
            double over = Math.min((double)(cigarettesToday - baselineCigarettes) / baselineCigarettes, 1.0);
            heartRate = baseHeartRate + (int) (over * (maxHeartRate - baseHeartRate));
        }
        resp.setHeartRate(heartRate + " bpm");

        // 8. Huyết áp động (nếu muốn show số cụ thể, thêm trường bloodPressure vào DTO)
        String bloodPressure;
        if (cigarettesToday == 0) bloodPressure = "118/78 mmHg";
        else if (cigarettesToday <= targetCigarettes) bloodPressure = "120/80 mmHg";
        else if (cigarettesToday <= baselineCigarettes) bloodPressure = "128/85 mmHg";
        else bloodPressure = "138/92 mmHg";
        resp.setBloodPressure(bloodPressure); // Nếu DTO chưa có, bạn bổ sung

        // 9. Huyết áp, tuần hoàn, da (feedback chữ)
        String heartRateStatus, bpStatus, circStatus, skinStatus;
        if (cigarettesToday == 0) {
            heartRateStatus = "Ổn định";
            bpStatus = "Ổn định mạnh";
            circStatus = "Ổn định";
            skinStatus = "Cải thiện";
        } else if (cigarettesToday <= targetCigarettes) {
            heartRateStatus = "Ổn định";
            bpStatus = "Ổn định";
            circStatus = "Ổn định";
            skinStatus = "Ổn định";
        } else if (cigarettesToday <= baselineCigarettes) {
            heartRateStatus = "Bình thường";
            bpStatus = "Bình thường";
            circStatus = "Bình thường";
            skinStatus = "Bình thường";
        } else {
            heartRateStatus = "Bất thường";
            bpStatus = "Bất thường";
            circStatus = "Bất thường";
            skinStatus = "Xấu đi";
        }
        resp.setHeartRateStatus(heartRateStatus);
        resp.setBloodPressureStatus(bpStatus);
        resp.setCirculationStatus(circStatus);
        resp.setSkinStatus(skinStatus);

        // 10. TÍNH CHỈ SỐ SỨC KHỎE THEO % (KEY LOGIC GAME HÓA)
        resp.setDailyHealthPercent(
                calculateDailyHealthPercent(cigarettesToday, targetCigarettes, baselineCigarettes)
        );

        return resp;
    }

    /**
     * Tính % chỉ số sức khỏe của ngày hôm đó (rất quan trọng cho dashboard động viên user)
     */
    private static int calculateDailyHealthPercent(int cigarettesToday, int targetCigarettes, int baselineCigarettes) {
        if (cigarettesToday == 0) return 100; // Không hút, hoàn hảo
        if (cigarettesToday < targetCigarettes) {
            double ratio = (double) cigarettesToday / (double) targetCigarettes;
            // Hút càng ít hơn mục tiêu càng tốt (96-99%)
            return 95 + (int) Math.round((1 - ratio) * 4);
        }
        if (cigarettesToday == targetCigarettes) return 95; // Hút đúng mục tiêu, vẫn có ảnh hưởng nhẹ
        if (cigarettesToday <= baselineCigarettes) {
            double overRatio = (double) (cigarettesToday - targetCigarettes) / (double) (baselineCigarettes - targetCigarettes + 1);
            int percent = 95 - (int) Math.round(overRatio * 40);
            return Math.max(percent, 60);
        }
        // Vượt baseline: càng hút nhiều, càng giảm % mạnh
        double overBaseline = (double) (cigarettesToday - baselineCigarettes) / baselineCigarettes;
        int percent = 60 - (int) Math.round(overBaseline * 40);
        return Math.max(percent, 30); // Không cho thấp hơn 30%
    }

}
