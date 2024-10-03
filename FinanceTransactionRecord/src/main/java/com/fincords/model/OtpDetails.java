package com.fincords.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "otp_details", indexes = {
        @Index(name = "idx_mobile_number", columnList = "mobileNumber")
})
public class OtpDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    private String latestOtp;

    @Column(nullable = false)
    private Integer failedAttempsPerDay = 0;

    @Column(nullable = false)
    private LocalDateTime sentAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
}
