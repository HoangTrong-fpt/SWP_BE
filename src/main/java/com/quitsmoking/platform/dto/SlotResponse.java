package com.quitsmoking.platform.dto;

public class SlotResponse {
    private Long id;
    private Long slotId;
    private Long customerId;
    private Long coachId;
    private String date;
    private String time;
    private String status;

    public SlotResponse(Long id, Long slotId, Long customerId, Long coachId, String date, String time, String status) {
        this.id = id;
        this.slotId = slotId;
        this.customerId = customerId;
        this.coachId = coachId;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 