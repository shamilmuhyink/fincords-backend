package com.fincords.service.implementation;

import com.fincords.model.OtpDetails;
import com.fincords.model.User;
import com.fincords.repository.OtpDetailsRepository;
import com.fincords.repository.UserRepository;
import com.fincords.service.AuthService;
import com.fincords.util.AuthUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    UserRepository userRepository;
    private OtpDetailsRepository otpDetailsRepo;

    @Override
    @Transactional
    public boolean sentOtp(String mobileNumber) {
        try {
            boolean isOtpSent = false;
            User user = userRepository.findByMobileNumber(mobileNumber)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setMobileNumber(mobileNumber);
                        return userRepository.save(newUser);
                    });
            String otp = AuthUtil.generateOtp();
            OtpDetails otpDetails = otpDetailsRepo.findByMobileNumber(mobileNumber)
                    .map(otpRecord -> {
                        otpRecord.setOtp(otp);
                        otpRecord.setOtpCount(otpRecord.getOtpCount() + 1);
                        return otpDetailsRepo.save(otpRecord);
                    })
                    .orElseGet(() -> {
                        OtpDetails otpRecord = new OtpDetails();
                        otpRecord.setOtp(otp);
                        otpRecord.setOtpCount(1);
                        otpRecord.setMobileNumber(mobileNumber);
                        return otpDetailsRepo.save(otpRecord);
                    });

            isOtpSent = true;
            return isOtpSent;
        }catch (Exception e) {
            log.severe("Error occurred while sending OTP: " + e.getMessage());
            return false;
        }
    }
}
