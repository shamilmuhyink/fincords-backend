package com.fincords.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    public String generateOtp(String mobileNumber);
    public boolean verifyOtp(String mobileNumber, String Otp);
    public void assignRole(String mobileNumber, String roleName);
    public void removeRole(String mobileNumber, String roleName);
}
