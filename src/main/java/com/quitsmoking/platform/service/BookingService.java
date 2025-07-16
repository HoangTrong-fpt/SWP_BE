package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.BookingRequest;
import com.quitsmoking.platform.dto.BookingResponse;
import com.quitsmoking.platform.dto.SlotResponse;
import com.quitsmoking.platform.dto.AppointmentResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Booking;
import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.repository.AccountRepository;
import com.quitsmoking.platform.repository.BookingRepository;
import com.quitsmoking.platform.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BookingService {


    private final   BookingRepository bookingRepo;
    private final   AccountRepository accountRepo;
    private final   CoachRepository coachRepository;
    @Autowired
    public BookingService(BookingRepository bookingRepo, AccountRepository accountRepo, CoachRepository coachRepository) {
        this.bookingRepo = bookingRepo;
        this.accountRepo = accountRepo;
        this.coachRepository = coachRepository;
    }

    public BookingResponse createBooking(BookingRequest request) {
        Account user = accountRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account coachAccount = accountRepo.findById(request.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCoach(coachAccount);
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime() != null ? LocalTime.parse(request.getStartTime()) : null);
        booking.setEndTime(request.getEndTime() != null ? LocalTime.parse(request.getEndTime()) : null);
        booking.setStatus("pending");
        booking.setCreatedAt(ZonedDateTime.now());
        booking.setUpdatedAt(ZonedDateTime.now());

        // Lấy link Google Meet từ entity Coach
        Coach coachEntity = coachRepository.findById(request.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach not found with id: " + request.getCoachId()));
        booking.setGoogleMeetLink(coachEntity.getGoogleMeetLink());

        bookingRepo.save(booking);

        return toResponse(booking);
    }

    public List<BookingResponse> getAll() {
        return bookingRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getByUser(Long userId) {
        return bookingRepo.findByUser_Id(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getByCoach(Long coachId) {
        return bookingRepo.findByCoach_Id(coachId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void cancelBooking(Long bookingId) {
        bookingRepo.deleteById(bookingId);
    }

    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (request.getUserId() != null) {
            Account user = accountRepo.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            booking.setUser(user);
        }
        if (request.getCoachId() != null) {
            Account coach = accountRepo.findById(request.getCoachId())
                    .orElseThrow(() -> new RuntimeException("Coach not found"));
            booking.setCoach(coach);
        }
        if (request.getDate() != null) {
            booking.setDate(request.getDate());
        }
        if (request.getStartTime() != null) {
            booking.setStartTime(LocalTime.parse(request.getStartTime()));
        }
        if (request.getEndTime() != null) {
            booking.setEndTime(LocalTime.parse(request.getEndTime()));
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Account account) {

                if (account.getRole() == Role.ADMIN || account.getRole() == Role.COACH) {
                    String newStatus = request.getStatus().toLowerCase();
                    booking.setStatus(newStatus);
                }
            }
        }

        booking.setUpdatedAt(ZonedDateTime.now());
        Booking updatedBooking = bookingRepo.save(booking);
        return toResponse(updatedBooking);
    }

    private BookingResponse toResponse(Booking b) {
        BookingResponse.UserInfo userInfo = new BookingResponse.UserInfo();
        userInfo.setCustomerId(b.getUser().getId());
        userInfo.setFullName(b.getUser().getFullName());
        userInfo.setEmail(b.getUser().getEmail());
        userInfo.setPhoneNumber(b.getUser().getPhoneNumber());
        userInfo.setAvatarUrl(b.getUser().getAvatarUrl());

        BookingResponse resp = new BookingResponse();
        resp.setBookingId(b.getId());
        resp.setCoachId(b.getCoach() != null ? b.getCoach().getId() : null);
        resp.setCoachName(b.getCoach().getFullName());
        resp.setDate(b.getDate() != null ? b.getDate().toString() : null);
        resp.setStartTime(b.getStartTime() != null ? b.getStartTime().toString() : null);
        resp.setEndTime(b.getEndTime() != null ? b.getEndTime().toString() : null);
        resp.setStatus(b.getStatus());
        resp.setCreatedAt(b.getCreatedAt() != null ? b.getCreatedAt().toString() : null);
        resp.setUpdatedAt(b.getUpdatedAt() != null ? b.getUpdatedAt().toString() : null);
        resp.setUser(userInfo);
        return resp;
    }

    public List<AppointmentResponse> getAllAppointments() {
        return bookingRepo.findAll().stream().map(b -> toAppointmentResponse(b, b.getUser().getFullName(), b.getUser().getAvatarUrl())).collect(Collectors.toList());
    }

    // MỚI: Thêm phương thức này để xử lý logic lọc từ Controller
    public List<BookingResponse> findBookingsByCriteria(Long coachId, String dateStr, String startTimeStr) {
        if (coachId == null || dateStr == null || startTimeStr == null) {
            return List.of();
        }

        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            List<String> statusesToFind = List.of("pending", "confirmed");

            List<Booking> bookings = bookingRepo.findByCoachIdAndDateAndStartTimeAndStatusIn(coachId, date, startTime, statusesToFind);
            return bookings.stream().map(this::toResponse).collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date or time format provided for filtering: " + e.getMessage());
            return List.of();
        }
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
                b.getEndTime() != null ? b.getEndTime().toString() : null,
                b.getStatus()
        );
    }

    public List<BookingResponse> findDailyBookedSlots(Long coachId, String dateStr) {
        if (coachId == null || dateStr == null) {
            return List.of();
        }
        try {
            LocalDate date = LocalDate.parse(dateStr);
            List<String> statusesToFind = List.of("pending", "confirmed");
            List<Booking> bookings = bookingRepo.findByCoachIdAndDateAndStatusIn(coachId, date, statusesToFind);
            return bookings.stream().map(this::toResponse).collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format for daily slots query: " + e.getMessage());
            return List.of();
        }
    }
}