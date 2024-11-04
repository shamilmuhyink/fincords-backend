package com.fincords.dto.respone;

import com.fincords.enumeration.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private TransactionType transactionType;
    private Double amount;
    private LocalDateTime transactionTime;
    private String description;
}
