package com.fincords.service.implementation;

import com.fincords.dto.respone.ResponseMaster;
import com.fincords.exception.AccountNotFoundException;
import com.fincords.exception.InvalidMobileNumberException;
import com.fincords.exception.RoleNotFoundException;
import com.fincords.exception.TwilioException;
import com.fincords.model.Account;
import com.fincords.model.Role;
import com.fincords.repository.AccountRepository;
import com.fincords.repository.RoleRepository;
import com.fincords.service.AuthService;
import com.fincords.util.JwtUtil;
import com.fincords.util.TwilioUtil;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TwilioUtil twilioUtil;

    @Override
    @Transactional
    public ResponseMaster generateOtp(String mobileNumber) {
        log.info("Generating OTP for mobile number: " + mobileNumber);
        try {
            // Validate mobile number
            if (!isValidMobileNumber(mobileNumber)) {
                log.warn("Invalid mobile number format: " + mobileNumber);
                throw new InvalidMobileNumberException(400, "Invalid mobile number format");
            }

            // Generate OTP
            String otp = generateRandomOtp();

            // Find or create user
            Account account = accountRepository.findByMobileNumber(mobileNumber)
                    .orElseGet(() -> {
                        Account newAccount = new Account();
                        newAccount.setMobileNumber(mobileNumber);
                        return newAccount;
                    });

            // Update user with OTP
            account.setOtp(otp);
            account.setOtpGeneratedAt(LocalDateTime.now());

            // Assign default role if not already assigned
            if (account.getRoles().isEmpty()) {
                Role userRole = roleRepository.findByName("ROLE_USER")
                        .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
                account.getRoles().add(userRole);
            }

            // Save user
            accountRepository.save(account);

            // Send OTP via Twilio
            boolean isOtpSent = twilioUtil.sendOtp(mobileNumber, otp);

            // Return response
            if (isOtpSent) {
                log.info("OTP sent successfully to mobile number: " + mobileNumber);
                return ResponseMaster.builder()
                        .status(HttpStatus.OK.value())
                        .message("OTP sent successfully")
                        .build();
            } else {
                log.error("Failed to send OTP to mobile number: " + mobileNumber);
                return ResponseMaster.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Failed to send OTP")
                        .build();
            }
        } catch (InvalidMobileNumberException e) {
            log.error("Invalid mobile number format: " + mobileNumber, e);
            return ResponseMaster.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Invalid mobile number format")
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate OTP for mobile number: " + mobileNumber, e);
            return ResponseMaster.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to generate OTP")
                    .build();
        }
    }

    private boolean isValidMobileNumber(String mobileNumber) {
        String mobileNumberPattern = "^[0-9+\\-\\(\\)\\.\\s]{3,20}$";
        return mobileNumber.matches(mobileNumberPattern);
    }

    @Override
    @Transactional
    public boolean verifyOtp(String mobileNumber, String otp) {
        Account account = accountRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (account.getOtp().equals(otp)) {
            accountRepository.save(account);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        Account account = accountRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile number: " + mobileNumber));

        return new User(account.getMobileNumber(), account.getOtp(),
                account.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public void assignRole(String mobileNumber, String roleName) {
        Account account = accountRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
        account.getRoles().add(role);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void removeRole(String mobileNumber, String roleName) {
        Account account = accountRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        account.getRoles().remove(role);
        accountRepository.save(account);
    }

    private String generateRandomOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
