package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoachRepository extends JpaRepository<Coach, Long> {
    Optional<Coach> findByAccountUsername(String username);
}
