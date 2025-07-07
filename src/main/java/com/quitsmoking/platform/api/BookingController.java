package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.BookingRequest;
import com.quitsmoking.platform.dto.BookingResponse;
import com.quitsmoking.platform.dto.SlotResponse;
import com.quitsmoking.platform.dto.AppointmentResponse;
import com.quitsmoking.platform.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private  BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAll() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getByUser(userId));
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<BookingResponse>> getByCoach(@PathVariable Long coachId) {
        return ResponseEntity.ok(bookingService.getByCoach(coachId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/slots")
    public ResponseEntity<List<SlotResponse>> getAllSlots() {
        return ResponseEntity.ok(bookingService.getAllSlots());
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(bookingService.getAllAppointments());
    }
}
