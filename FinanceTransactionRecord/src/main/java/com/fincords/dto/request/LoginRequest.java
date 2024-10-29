package com.fincords.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String mobileNumber;
    private String otp;
}
