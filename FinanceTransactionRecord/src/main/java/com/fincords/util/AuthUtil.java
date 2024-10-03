package com.fincords.util;

import java.security.SecureRandom;

public class AuthUtil {

    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp() {
        String otp = String.format("%06d", random.nextInt(999999));
        return otp;
    }
}
