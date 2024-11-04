package com.fincords.service.implementation;

import com.fincords.dto.request.OtpRequestDTO;
import com.fincords.dto.request.OtpVerificationDTO;
import com.fincords.dto.respone.ApiResponse;
import com.fincords.dto.respone.AuthResponseDTO;
import com.fincords.exception.AuthException;
import com.fincords.exception.InvalidMobileNumberException;
import com.fincords.model.Account;
import com.fincords.model.Role;
import com.fincords.repository.AccountRepository;
import com.fincords.repository.RoleRepository;
import com.fincords.service.AuthService;
import com.fincords.util.JwtUtil;
import com.fincords.util.TwilioUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TwilioUtil twilioUtil;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Transactional
    @Override
    public ApiResponse<?> sendOtp(OtpRequestDTO request) {
        log.info("Generating OTP for mobile number: " + request);
        String mobileNumber = request.getMobileNumber();
        try {
            // Generate OTP
            String otp = generateRandomOtp();

            // Find or create user
            Account account = accountRepo.findByMobileNumber(mobileNumber)
                    .orElseGet(() -> {
                        Account newAccount = new Account();
                        newAccount.setMobileNumber(mobileNumber);
                        return newAccount;
                    });

            // Update user with OTP
            account.setOtp(otp);
            account.setOtpExpireAt(LocalDateTime.now().plusSeconds(expiration));

            // Assign default role if not already assigned
            if (account.getRoles().isEmpty()) {
                Role userRole = roleRepo.findByName("USER")
                        .orElseGet(() -> roleRepo.save(new Role("USER")));
                account.getRoles().add(userRole);
            }

            // Save user
            accountRepo.save(account);

            // Send OTP via Twilio
            boolean isOtpSent = twilioUtil.sendOtp(mobileNumber, otp);

            // Return response
            if (isOtpSent) {
                log.info("OTP sent successfully to mobile number: " + mobileNumber);
                return ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("OTP sent successfully")
                        .timestamp(LocalDateTime.now())
                        .build();
            } else {
                log.error("Failed to send OTP to mobile number: " + mobileNumber);
                return ApiResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Failed to send OTP")
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (InvalidMobileNumberException e) {
            log.error("Invalid mobile number format: " + mobileNumber, e);
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate OTP for mobile number: " + mobileNumber, e);
            return ApiResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    @Transactional
    @Override
    public ApiResponse<?> verifyOtp(OtpVerificationDTO request) {
        try{
            String mobileNumber = request.getMobileNumber();
            log.info("Verifying OTP for mobile number: {}", maskMobileNumber(mobileNumber));

            Account account = accountRepo.findByMobileNumber(mobileNumber)
                    .orElseThrow(() -> new AuthException("User not found", HttpStatus.NOT_FOUND));

            if (account.getOtpExpireAt().isBefore(LocalDateTime.now())) {
                throw new AuthException("OTP has expired", HttpStatus.BAD_REQUEST);
            }

            if (!account.getOtp().equals(request.getOtp())) {
                throw new AuthException("Invalid OTP", HttpStatus.BAD_REQUEST);
            }

            UserDetails userDetails = loadUserByUsername(mobileNumber);

            String token = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            log.info("OTP verified successfully for mobile number: {}", maskMobileNumber(mobileNumber));

            AuthResponseDTO authDTO = AuthResponseDTO.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .mobileNumber(mobileNumber)
                    .expiresIn(jwtUtil.getExpirationTime())
                    .build();

            return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("OTP sent successfully")
                    .data(authDTO)
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error verifying OTP: ", e);
            throw new AuthException("Failed to verify OTP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String maskMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.length() < 4) {
            return "****";
        }
        return "******" + mobileNumber.substring(mobileNumber.length() - 4);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        Account account = accountRepo.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile number: " + mobileNumber));

        return new User(account.getMobileNumber(), account.getOtp(),
                account.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getId().toString())).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public void assignRole(String mobileNumber, String roleName) {
        Account account = accountRepo.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepo.findByName(roleName)
                .orElseGet(() -> roleRepo.save(new Role(roleName)));
        account.getRoles().add(role);
        accountRepo.save(account);
    }

    @Override
    @Transactional
    public void removeRole(String mobileNumber, String roleName) {
        Account account = accountRepo.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        account.getRoles().remove(role);
        accountRepo.save(account);
    }

    private String generateRandomOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
