package com.fincords.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class MobileRequest {
    @Pattern(regexp = "^[0-9+\\-\\(\\)\\.\\s]{3,20}$", message = "Invalid mobile number format")
    private String mobileNumber;
}
