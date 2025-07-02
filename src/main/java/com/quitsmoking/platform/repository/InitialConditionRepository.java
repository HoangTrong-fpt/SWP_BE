package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InitialConditionRepository extends JpaRepository<InitialCondition, Long> {
    Optional<InitialCondition> findByAccount(Account account);
}
