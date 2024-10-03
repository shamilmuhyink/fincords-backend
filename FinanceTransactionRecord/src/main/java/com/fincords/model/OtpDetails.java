package com.fincords.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "otp_details")
public class OtpDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    private int otpCount = 1;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
