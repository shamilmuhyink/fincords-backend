package com.fincords.util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log
@Component
public class TwilioUtil {
    @Value("${twilio.sid}")
    public String ACCOUNT_SID;

    @Value("${twilio.token}")
    public String AUTH_TOKEN;

    @Value("${twilio.number}")
    public String TWILIO_NUMBER;

    public boolean sendOtp(String mobileNumber, String otp) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(mobileNumber),
                    new com.twilio.type.PhoneNumber(TWILIO_NUMBER),
                    "OTP: " + otp).create();
            return true;
        } catch (Exception e) {
            log.severe("Failed to send OTP: " + e.getMessage());
            return false;
        }
    }
}
