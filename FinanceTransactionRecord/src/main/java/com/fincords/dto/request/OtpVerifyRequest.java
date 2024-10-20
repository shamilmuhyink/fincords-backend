package com.fincords.dto.request;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String mobileNumber;
    private String otp;
}
