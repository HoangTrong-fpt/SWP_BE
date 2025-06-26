package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    Optional<ForgotPassword> findByOtpAndAccount(Integer otp, Account account);
    void deleteById(Long id);
}
