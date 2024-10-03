package com.fincords.controller;

import com.fincords.requestDTO.OtpRequest;
import com.fincords.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/sentOtp")
    public ResponseEntity<?> sentOtp(@RequestBody OtpRequest otpRequest) {
        boolean isOtpSent = authService.sentOtp(otpRequest.getMobileNumber());
        return ResponseEntity.ok(isOtpSent);
    }
}
