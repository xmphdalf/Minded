package encrydecry;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password encryption utility using MD5 hashing.
 * Provides methods for hashing passwords and verifying password matches.
 *
 * @author Minded Team
 * @version 2.0
 */
public class endestr {

    /**
     * Hash a password using MD5 algorithm.
     *
     * @param password Plain text password to hash
     * @return MD5 hash in hexadecimal format, or null if hashing fails
     */
    public static String getMd5(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();

            // Convert bytes to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("MD5 algorithm not available: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verify that a plain text password matches a hashed password.
     *
     * @param plainPassword Plain text password to verify
     * @param hashedPassword MD5 hashed password to compare against
     * @return true if passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        String hashedInput = getMd5(plainPassword);
        return hashedInput != null && hashedInput.equals(hashedPassword);
    }

    /**
     * Legacy method for backward compatibility.
     *
     * @deprecated Use {@link #getMd5(String)} instead
     */
    @Deprecated
    public String encdecme(String str) {
        return getMd5(str);
    }
}


