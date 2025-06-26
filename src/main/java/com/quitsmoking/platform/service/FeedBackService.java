package com.quitsmoking.platform.service;

package com.example.demo.service;

import com.example.demo.model.Feedback;
import com.example.demo.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Create or update feedback
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    // Get all feedback
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    // Get feedback by ID
    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    // Delete feedback by ID
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}
