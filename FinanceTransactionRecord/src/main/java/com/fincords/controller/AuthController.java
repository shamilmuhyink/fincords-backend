package com.fincords.controller;

import com.fincords.dto.request.LoginRequest;
import com.fincords.dto.request.MobileRequest;
import com.fincords.dto.respone.ResponseMaster;
import com.fincords.exception.InvalidMobileNumberException;
import com.fincords.service.AuthService;
import com.fincords.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("v1/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/request-otp")
    public ResponseEntity<?> generateOtp(@RequestBody MobileRequest request) {
        String mobileNumber = request.getMobileNumber();
        ResponseMaster response  = authService.generateOtp(mobileNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody LoginRequest request) {
        String mobileNumber = request.getMobileNumber();
        String otp = request.getOtp();

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
