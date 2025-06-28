package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.FeedbackRequest;
import com.quitsmoking.platform.dto.FeedbackResponse;
import com.quitsmoking.platform.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // ✅ CUSTOMER có quyền gửi phản hồi
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<FeedbackResponse> submitFeedback(@RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.addFeedback(request));
    }

    // ✅ ADMIN, CUSTOMER, COACH có quyền xem phản hồi
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'COACH')")
    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacks(@PathVariable Long blogId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByBlogId(blogId));
    }
}
