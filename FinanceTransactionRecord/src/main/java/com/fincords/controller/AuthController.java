package com.fincords.controller;

import com.fincords.dto.request.OtpRequestDTO;
import com.fincords.dto.request.OtpVerificationDTO;
import com.fincords.dto.respone.ApiResponse;
import com.fincords.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("v1/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/request-otp")
    public ResponseEntity<ApiResponse<Object>> sendOtp(@Validated @RequestBody OtpRequestDTO request) {
        ApiResponse<Object> response  = authService.sendOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Object>> verifyOtp(@Validated @RequestBody OtpVerificationDTO request) {
        ApiResponse<Object> response = authService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRole(@RequestBody Map<String, String> request) {
        String mobileNumber = request.get("mobileNumber");
        String roleName = request.get("roleName");
        authService.assignRole(mobileNumber, roleName);
        return ResponseEntity.ok(Map.of("message", "Role assigned successfully"));
    }

    @PostMapping("/remove-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeRole(@RequestBody Map<String, String> request) {
        String mobileNumber = request.get("mobileNumber");
        String roleName = request.get("roleName");
        authService.removeRole(mobileNumber, roleName);
        return ResponseEntity.ok(Map.of("message", "Role removed successfully"));
    }

}
