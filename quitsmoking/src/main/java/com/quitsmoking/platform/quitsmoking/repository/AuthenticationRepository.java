package com.quitsmoking.platform.quitsmoking.repository;

import com.quitsmoking.platform.quitsmoking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationRepository extends JpaRepository<Account, Long> {

    Account findById(long id);


    Optional<Account> findByUsername(String username);
}
