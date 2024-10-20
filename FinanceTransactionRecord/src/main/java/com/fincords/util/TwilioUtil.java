package com.fincords.util;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;

@Log
public class TwilioUtil {
//    @Value("${twilio.sid}")
//    static String ACCOUNT_SID;
//
//    @Value("${twilio.token}")
//    static String AUTH_TOKEN;
//
//    @Value("${twilio.number}")
//    static String TWILIO_NUMBER;

    private static final String ACCOUNT_SID = "AC56f3436d05f4c43f5b15875271a4e260";
    private static final String AUTH_TOKEN = "c28d5ffbe46d2367e82f78d93d8aba2b";
    private static final String TWILIO_NUMBER = "+12075032978";

    public static boolean sentOtp(String mobileNumber, String otp){
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
