package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByBlogId(Long blogId);
}
