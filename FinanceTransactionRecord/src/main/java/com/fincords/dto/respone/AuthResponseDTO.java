package com.fincords.dto.respone;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private String token;
    private String mobileNumber;
    private String refreshToken;
    private long expiresIn;
}
