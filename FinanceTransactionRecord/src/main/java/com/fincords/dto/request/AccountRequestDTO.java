package com.fincords.dto.request;

import com.fincords.enumeration.TransactionType;
import lombok.Data;

@Data
public class AccountRequestDTO {
    private String name;
    private String mobileNumber;
    private Double amount;
    private TransactionType type;
    private String description;
}
