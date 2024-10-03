package com.fincords.repository;

import com.fincords.model.OtpDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpDetailsRepository extends JpaRepository<OtpDetails, Long> {
    Optional<OtpDetails> findByMobileNumber(String mobileNumber);
}
