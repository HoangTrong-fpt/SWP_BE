package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.FeedbackRequest;
import com.quitsmoking.platform.dto.FeedbackResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Blog;
import com.quitsmoking.platform.entity.Feedback;
import com.quitsmoking.platform.repository.AccountRepository;
import com.quitsmoking.platform.repository.BlogRepository;
import com.quitsmoking.platform.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepo;
    private final AccountRepository accountRepo;
    private final BlogRepository blogRepo;

    // Constructor Injection để tiêm các repository vào FeedbackService
    public FeedbackService(FeedbackRepository feedbackRepo,
                           AccountRepository accountRepo,
                           BlogRepository blogRepo) {
        this.feedbackRepo = feedbackRepo;
        this.accountRepo = accountRepo;
        this.blogRepo = blogRepo;
    }

    // Phương thức thêm feedback
    public FeedbackResponse addFeedback(FeedbackRequest request) {
        Account account = accountRepo.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Blog blog = blogRepo.findById(request.getBlogId())
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        Feedback feedback = new Feedback();
        feedback.setAccount(account);
        feedback.setBlog(blog);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepo.save(feedback);

        return new FeedbackResponse(
                account.getFullName(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getCreatedAt()
        );
    }

    // Phương thức lấy danh sách feedback theo ID của blog
    public List<FeedbackResponse> getFeedbacksByBlogId(Long blogId) {
        return feedbackRepo.findByBlogId(blogId)
                .stream()
                .map(fb -> new FeedbackResponse(
                        fb.getAccount().getFullName(),
                        fb.getRating(),
                        fb.getComment(),
                        fb.getCreatedAt()))
                .toList();
    }

    // Phương thức cập nhật feedback
    public FeedbackResponse updateFeedback(Long id, Feedback updatedFeedback) {
        Feedback existing = feedbackRepo.findById(id)  // Dùng feedbackRepo thay vì feedbackRepository
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        // Cập nhật các trường cần thiết
        existing.setComment(updatedFeedback.getComment()); // Cập nhật nội dung feedback
        existing.setRating(updatedFeedback.getRating()); // Cập nhật rating nếu cần

        // Lưu lại feedback đã sửa và trả về phản hồi
        Feedback saved = feedbackRepo.save(existing);
        return new FeedbackResponse(
                saved.getAccount().getFullName(),
                saved.getRating(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }

    // Phương thức xóa feedback theo ID
    public void deleteFeedback(Long id) {
        if (!feedbackRepo.existsById(id)) {  // Kiểm tra feedback tồn tại không
            throw new RuntimeException("Feedback not found");
        }
        feedbackRepo.deleteById(id);  // Xóa feedback theo ID
    }
}
