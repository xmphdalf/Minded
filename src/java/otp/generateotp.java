package otp;

import java.security.SecureRandom;

/**
 * Secure OTP (One-Time Password) generator.
 * Generates cryptographically secure random 4-digit numeric codes.
 *
 * @author Minded Team
 * @version 2.0
 */
public class generateotp {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int OTP_LENGTH = 4;
    private static final int OTP_MAX = 10000; // 10^4 for 4 digits

    /**
     * Generate a secure 4-digit OTP using cryptographically strong random number generator.
     *
     * @return 4-digit OTP as String (zero-padded if necessary)
     */
    public static String generateOTP() {
        int randomPin = secureRandom.nextInt(OTP_MAX);
        return String.format("%0" + OTP_LENGTH + "d", randomPin);
    }

    /**
     * Validate OTP format (4 digits, numeric only).
     *
     * @param otp OTP string to validate
     * @return true if valid format, false otherwise
     */
    public static boolean isValidOTPFormat(String otp) {
        if (otp == null || otp.length() != OTP_LENGTH) {
            return false;
        }
        return otp.matches("\\d{" + OTP_LENGTH + "}");
    }

    /**
     * Legacy method for backward compatibility.
     *
     * @deprecated Use {@link #generateOTP()} instead
     */
    @Deprecated
    public String getotp() {
        return generateOTP();
    }
}
