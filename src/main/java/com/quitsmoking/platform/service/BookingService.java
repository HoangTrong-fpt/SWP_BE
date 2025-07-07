package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.BookingRequest;
import com.quitsmoking.platform.dto.BookingResponse;
import com.quitsmoking.platform.dto.SlotResponse;
import com.quitsmoking.platform.dto.AppointmentResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Booking;
import com.quitsmoking.platform.repository.AccountRepository;
import com.quitsmoking.platform.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {


    private final   BookingRepository bookingRepo;
    private final   AccountRepository accountRepo;
    @Autowired
    public BookingService(BookingRepository bookingRepo, AccountRepository accountRepo) {
        this.bookingRepo = bookingRepo;
        this.accountRepo = accountRepo;
    }

    public BookingResponse createBooking(BookingRequest request) {
        Account user = accountRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account coach = accountRepo.findById(request.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCoach(coach);
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime() != null ? LocalTime.parse(request.getStartTime()) : null);
        booking.setEndTime(request.getEndTime() != null ? LocalTime.parse(request.getEndTime()) : null);
        booking.setStatus("pending");
        booking.setCreatedAt(ZonedDateTime.now());
        booking.setUpdatedAt(ZonedDateTime.now());

        bookingRepo.save(booking);

        return toResponse(booking);
    }

    public List<BookingResponse> getAll() {
        return bookingRepo.findAll().stream().map(this::toResponse).toList();
    }

    public List<BookingResponse> getByUser(Long userId) {
        return bookingRepo.findByUser_Id(userId).stream().map(this::toResponse).toList();
    }

    public List<BookingResponse> getByCoach(Long coachId) {
        return bookingRepo.findByCoach_Id(coachId).stream().map(this::toResponse).toList();
    }

    public void cancelBooking(Long bookingId) {
        bookingRepo.deleteById(bookingId);
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getUser().getFullName(),
                b.getCoach().getFullName(),
                b.getDate(),
                b.getStartTime(),
                b.getEndTime(),
                b.getStatus(),
                b.getCreatedAt(),
                b.getUpdatedAt()
        );
    }

    public SlotResponse createSlot(Booking booking) {
        return toSlotResponse(booking);
    }

    public AppointmentResponse createAppointment(Booking booking, String name, String avatar) {
        return toAppointmentResponse(booking, name, avatar);
    }

    public List<SlotResponse> getAllSlots() {
        return bookingRepo.findAll().stream().map(this::toSlotResponse).toList();
    }

    public List<AppointmentResponse> getAllAppointments() {
        return bookingRepo.findAll().stream().map(b -> toAppointmentResponse(b, "", "")).toList();
    }

    private SlotResponse toSlotResponse(Booking b) {
        return new SlotResponse(
            b.getId(),
            null,
            b.getUser() != null ? b.getUser().getId() : null,
            b.getCoach() != null ? b.getCoach().getId() : null,
            b.getDate() != null ? b.getDate().toString() : null,
            b.getStartTime() != null ? b.getStartTime().toString() : null,
            b.getStatus()
        );
    }

    private AppointmentResponse toAppointmentResponse(Booking b, String name, String avatar) {
        return new AppointmentResponse(
            b.getCreatedAt() != null ? b.getCreatedAt().toString() : null,
            name,
            avatar,
            b.getId(),
            b.getCoach() != null ? b.getCoach().getId() : null,
            b.getUser() != null ? b.getUser().getId() : null,
            b.getDate() != null ? b.getDate().toString() : null,
            b.getStartTime() != null ? b.getStartTime().toString() : null,
            b.getStatus()
        );
    }
}
