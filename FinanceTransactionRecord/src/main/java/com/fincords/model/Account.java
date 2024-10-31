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

    @Column(unique = true, nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    private String Otp;

    @Column(nullable = false)
    private LocalDateTime OtpExpiryTime;

    @Column(nullable = false)
    private LocalDateTime accountCreatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "account_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "fromAccount")
    private Set<Transaction> fromTransactions = new HashSet<>();

    @OneToMany(mappedBy = "toAccount")
    private Set<Transaction> toTransactions = new HashSet<>();
}
