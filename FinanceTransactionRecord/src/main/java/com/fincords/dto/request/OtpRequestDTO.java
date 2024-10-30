package com.fincords.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class OtpRequestDTO  {
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9+\\-\\(\\)\\.\\s]{3,20}$", message = "Invalid mobile number format")
    private String mobileNumber;
}
