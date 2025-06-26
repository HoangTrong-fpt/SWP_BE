package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InitialConditionRepository extends JpaRepository<InitialCondition, Long> {
    Optional<InitialCondition> findByAccountAndIsActiveTrue(Account account);
    @Query("SELECT MAX(i.version) FROM InitialCondition i WHERE i.account = :account")
    Optional<Integer> findMaxVersionByAccount(@Param("account") Account account);
}
