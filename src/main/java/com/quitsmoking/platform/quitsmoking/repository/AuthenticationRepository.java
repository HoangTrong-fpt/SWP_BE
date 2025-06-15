package com.quitsmoking.platform.quitsmoking.repository;

import com.quitsmoking.platform.quitsmoking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthenticationRepository extends JpaRepository<Account, Long> {

    Account findAccountByUsername(String username);

    boolean existsByUsername(String username);

    List<Account> findByActiveTrue();

    //=========================dev code ==========================================
    @Modifying
    @Query(value = "ALTER TABLE account AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
    //============================================================================
}
