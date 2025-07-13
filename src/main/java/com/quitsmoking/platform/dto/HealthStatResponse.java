    package com.quitsmoking.platform.dto;

    import lombok.Data;

    @Data
    public class HealthStatResponse {
        private int cigarettesToday;
        private int targetCigarettes;
        private double nicotineEstimate;
        private String coStatus;
        private int moneySavedToday;
        private String lungStatus;
        private String tasteStatus;
        private boolean isTargetAchieved;
        private String bloodPressureStatus;
        private String bloodPressure;
        private String circulationStatus;
        private String skinStatus;
        private String heartRate;
        private String heartRateStatus;
        private int dailyHealthPercent;
    }
