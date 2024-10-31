package com.fincords.model;

import com.fincords.enumeration.TransactionStatus;
import com.fincords.enumeration.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account", nullable = false)
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account", nullable = false)
    private Account toAccount;

    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTate;

    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "description", nullable = true)
    private String description;
}
