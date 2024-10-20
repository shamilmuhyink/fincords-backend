package com.fincords.repository;

import com.fincords.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.isActive = true AND a.mobileNumber = :mobileNumber")
    Optional<Account> findByMobileNumber(@Param("mobileNumber") String mobileNumber);
}
