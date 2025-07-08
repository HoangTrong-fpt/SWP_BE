package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.FeedbackRequest;
import com.quitsmoking.platform.dto.FeedbackResponse;
import com.quitsmoking.platform.dto.FeedbackUpdateRequest;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.entity.Blog;
import com.quitsmoking.platform.repository.AccountRepository;
import com.quitsmoking.platform.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService  {

    private final FeedbackRepository feedbackRepo;
    private final AccountRepository accountRepo;
    private final BlogRepository blogRepo;

    public FeedbackService(FeedbackRepository feedbackRepo,
                           AccountRepository accountRepo,
                           BlogRepository blogRepo) {
        this.feedbackRepo = feedbackRepo;
        this.accountRepo = accountRepo;
        this.blogRepo = blogRepo;
    }

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
                feedback.getId(),
                feedback.getComment(),
                feedback.getRating(),
                blog.getId(),
                account.getId(),
                new FeedbackResponse.User(
                        account.getId(),
                        account.getUsername(),
                        account.getFullName(),
                        account.getAvatarUrl()
                ),
                feedback.getCreatedAt()
        );
    }


    public List<FeedbackResponse> getFeedbacksByBlogId(Long blogId) {
        return feedbackRepo.findByBlogId(blogId)
                .stream()
                .map(fb -> new FeedbackResponse(
                        fb.getId(),
                        fb.getComment(),
                        fb.getRating(),
                        fb.getBlog().getId(),
                        fb.getAccount().getId(),
                        new FeedbackResponse.User(
                                fb.getAccount().getId(),
                                fb.getAccount().getUsername(),
                                fb.getAccount().getFullName(),
                                fb.getAccount().getAvatarUrl()
                        ),
                        fb.getCreatedAt()))
                .toList();
    }

    public FeedbackResponse updateFeedback(FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        // Nếu muốn cập nhật thời gian sửa:
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepo.save(feedback);

        return new FeedbackResponse(
                feedback.getId(),
                feedback.getComment(),
                feedback.getRating(),
                feedback.getBlog().getId(),
                feedback.getAccount().getId(),
                new FeedbackResponse.User(
                        feedback.getAccount().getId(),
                        feedback.getAccount().getUsername(),
                        feedback.getAccount().getFullName(),
                        feedback.getAccount().getAvatarUrl()
                ),
                feedback.getCreatedAt()
        );
    }

    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedbackRepo.delete(feedback);
    }
}