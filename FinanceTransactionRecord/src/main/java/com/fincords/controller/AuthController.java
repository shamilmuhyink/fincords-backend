package com.fincords.controller;

import com.fincords.dto.request.MobileRequest;
import com.fincords.service.AuthService;
import com.fincords.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("v1/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/generate-otp")
    public ResponseEntity<?> generateOtp(@RequestBody MobileRequest request) {
        String mobileNumber = request.getMobileNumber();
        String otp = authService.generateOtp(mobileNumber);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String mobileNumber = request.get("mobileNumber");
        String otp = request.get("otp");

        if (authService.verifyOtp(mobileNumber, otp)) {
            UserDetails userDetails = authService.loadUserByUsername(mobileNumber);
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid OTP"));
        }
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
