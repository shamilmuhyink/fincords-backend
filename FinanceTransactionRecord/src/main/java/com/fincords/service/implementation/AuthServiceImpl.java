package com.fincords.service.implementation;

import com.fincords.model.OtpDetails;
import com.fincords.repository.OtpDetailsRepository;
import com.fincords.repository.UserRepository;
import com.fincords.service.AuthService;
import com.fincords.util.AuthUtil;
import com.fincords.util.TwilioUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Log
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private OtpDetailsRepository otpDetailsRepo;

    @Override
    @Transactional
    public boolean sentOtp(String mobileNumber) {
        try {
            String otp = AuthUtil.generateOtp();
            OtpDetails otpDetails = otpDetailsRepo.findByMobileNumber(mobileNumber).orElse(new OtpDetails());
            otpDetails.setMobileNumber(mobileNumber);
            otpDetails.setLatestOtp(otp);
            otpDetails.setSentAt(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
            otpDetailsRepo.save(otpDetails);
            boolean isOtpSent = TwilioUtil.sentOtp(mobileNumber, otp);
            return isOtpSent;
        }catch (Exception e) {
            log.severe("Error occurred while sending OTP: " + e.getMessage());
            return false;
        }
    }
}
