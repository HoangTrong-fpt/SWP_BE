package com.quitsmoking.platform.repository;


import com.quitsmoking.platform.entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthenticationRepository extends JpaRepository<Account, Long> {

    Optional<Account> findAccountByUsername(String username);

    Optional<Account> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE Account a SET a.password = ?2 WHERE a.email = ?1")
    void updatePassword(String email, String password);

    List<Account> findByRole(com.quitsmoking.platform.enums.Role role);
}