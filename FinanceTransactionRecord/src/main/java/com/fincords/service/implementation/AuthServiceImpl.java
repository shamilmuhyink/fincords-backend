package com.fincords.service.implementation;

import com.fincords.model.Account;
import com.fincords.model.Role;
import com.fincords.repository.AccountRepository;
import com.fincords.repository.RoleRepository;
import com.fincords.service.AuthService;
import com.fincords.util.JwtUtil;
import com.fincords.util.TwilioUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Collectors;

@Log
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public String generateOtp(String mobileNumber) {
//        Generate OTP
        String otp = generateRandomOtp();

//        Create user if not found in records
        Account account = accountRepository.findByMobileNumber(mobileNumber)
                .orElse(new Account());

        account.setMobileNumber(mobileNumber);
        account.setOtp(otp);
        account.setOtpGeneratedAt(LocalDateTime.now());
        
        // Set default role for new users
        if (account.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            account.getRoles().add(userRole);
        }

        accountRepository.save(account);

        // In a real application, you would send this OTP via SMS
        boolean isOtpSent = TwilioUtil.sentOtp(mobileNumber, otp);
        return otp;
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
