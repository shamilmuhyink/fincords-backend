package com.fincords.util;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;

public class TwilioUtil {
    public static final String ACCOUNT_SID = "AC56f3436d05f4c43f5b15875271a4e260";
    public static final String AUTH_TOKEN = "fddccb800a082e04a21314f340173d12";
    public static final String TWILIO_NUMBER = "+12075032978";

    public static boolean sentOtp(String mobileNumber, String otp){
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(mobileNumber),
                    new com.twilio.type.PhoneNumber(TWILIO_NUMBER),
                    "OTP: " + otp).create();

            System.out.println("OTP sent with SID: " + message.getSid());
            return true;
        } catch (ApiException e) {
            System.err.println("Failed to send OTP: " + e.getMessage());
            return false;
        }
    }
}
