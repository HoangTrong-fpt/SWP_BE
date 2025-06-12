package com.quitsmoking.platform.quitsmoking.repository;

import com.quitsmoking.platform.quitsmoking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<Account, Long> {

    Account findAccountByUsername(String username);
}
