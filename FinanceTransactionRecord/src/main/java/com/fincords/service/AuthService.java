package com.fincords.service;

import com.fincords.dto.request.OtpRequestDTO;
import com.fincords.dto.request.OtpVerificationDTO;
import com.fincords.dto.respone.ApiResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    public ApiResponse<?> sendOtp(OtpRequestDTO request);
    public ApiResponse<?> verifyOtp(OtpVerificationDTO request);
    public void assignRole(String mobileNumber, String roleName);
    public void removeRole(String mobileNumber, String roleName);
}
