package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.AppointmentResponse;
import com.quitsmoking.platform.dto.BookingRequest;
import com.quitsmoking.platform.dto.BookingResponse;
import com.quitsmoking.platform.dto.SlotResponse;
import com.quitsmoking.platform.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<BookingResponse> create(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAll() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getByUser(userId));
    }

    @PreAuthorize("hasAnyRole( 'ADMIN', 'COACH')")
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<BookingResponse>> getByCoach(@PathVariable Long coachId) {
        return ResponseEntity.ok(bookingService.getByCoach(coachId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> update(@PathVariable Long id, @RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }


    // SỬA: Thay thế phương thức getAllAppointments cũ bằng phương thức này
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'COACH')")
    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointmentsByCriteria(
            @RequestParam(required = false) Long coachId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String time) {

        // Nếu có 'time', kiểm tra một khung giờ cụ thể
        if (time != null) {
            List<BookingResponse> specificBooking = bookingService.findBookingsByCriteria(coachId, date, time);
            return ResponseEntity.ok(specificBooking);
        }

        // Nếu chỉ có 'date' và 'coachId', lấy tất cả các giờ bận trong ngày
        if (coachId != null && date != null) {
            List<BookingResponse> dailyBookedSlots = bookingService.findDailyBookedSlots(coachId, date);
            return ResponseEntity.ok(dailyBookedSlots);
        }

        // Mặc định, trả về tất cả
        return ResponseEntity.ok(bookingService.getAllAppointments());
    }
}