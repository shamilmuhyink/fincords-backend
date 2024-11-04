package com.fincords.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mobile_number", unique = true)
    private String mobileNumber;

    @Column(name = "otp", nullable = true, length = 6)
    private String otp;

    @Column(name = "otp_expire_at", nullable = true)
    private LocalDateTime otpExpireAt;

    @Column(name = "account_created_at", nullable = false)
    private LocalDateTime AccountCreatedAt = LocalDateTime.now();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "account_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "fromAccount", fetch = FetchType.EAGER)
    private Set<Transaction> fromTransactions = new HashSet<>();

    @OneToMany(mappedBy = "toAccount", fetch = FetchType.EAGER)
    private Set<Transaction> toTransactions = new HashSet<>();
}
