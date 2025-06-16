package com.quitsmoking.platform.repository;


import com.quitsmoking.platform.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthenticationRepository extends JpaRepository<Account, Long> {

    Account findAccountByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Account> findByEmail(String email);

    List<Account> findByActiveTrue();

    //=========================dev code ==========================================
    @Modifying
    @Query(value = "ALTER TABLE account AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
    //============================================================================
}
