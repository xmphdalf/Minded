package otp;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for generateotp OTP generation utility.
 * Tests secure random OTP generation and validation.
 *
 * @author Minded Team
 */
public class GenerateOtpTest {

    /**
     * Test 1: OTP generation produces 4-digit strings.
     */
    @Test
    public void testGenerateOTPLength() {
        System.out.println("Test: OTP generation length");

        String otp = generateotp.generateOTP();
        assertNotNull("OTP should not be null", otp);
        assertEquals("OTP should be 4 characters", 4, otp.length());

        System.out.println("✓ OTP has correct length (4 digits)");
    }

    /**
     * Test 2: OTP contains only numeric characters.
     */
    @Test
    public void testGenerateOTPNumericOnly() {
        System.out.println("Test: OTP contains only numeric characters");

        String otp = generateotp.generateOTP();
        assertTrue("OTP should contain only digits", otp.matches("\\d{4}"));

        System.out.println("✓ OTP contains only numeric characters");
    }

    /**
     * Test 3: Multiple OTP generations produce different values (randomness).
     */
    @Test
    public void testGenerateOTPRandomness() {
        System.out.println("Test: OTP randomness");

        Set<String> otps = new HashSet<>();
        int testCount = 100;

        for (int i = 0; i < testCount; i++) {
            otps.add(generateotp.generateOTP());
        }

        // With 10000 possible values and 100 generations, we expect high uniqueness
        // Allow for some small chance of collision
        assertTrue("Should generate mostly unique OTPs",
                otps.size() > testCount * 0.95); // At least 95% unique

        System.out.println("✓ OTP generation is random (" + otps.size() + "/" + testCount + " unique)");
    }

    /**
     * Test 4: OTP is zero-padded to 4 digits.
     */
    @Test
    public void testGenerateOTPZeroPadding() {
        System.out.println("Test: OTP zero-padding");

        // Generate many OTPs and check they're all 4 digits
        for (int i = 0; i < 50; i++) {
            String otp = generateotp.generateOTP();
            assertEquals("OTP should always be 4 digits (zero-padded)", 4, otp.length());
            assertTrue("OTP should be numeric", otp.matches("\\d{4}"));
        }

        System.out.println("✓ OTP is properly zero-padded");
    }

    /**
     * Test 5: OTP format validation - valid OTPs.
     */
    @Test
    public void testIsValidOTPFormatValid() {
        System.out.println("Test: Valid OTP format validation");

        assertTrue("'0000' should be valid", generateotp.isValidOTPFormat("0000"));
        assertTrue("'1234' should be valid", generateotp.isValidOTPFormat("1234"));
        assertTrue("'9999' should be valid", generateotp.isValidOTPFormat("9999"));
        assertTrue("'0001' should be valid", generateotp.isValidOTPFormat("0001"));

        System.out.println("✓ Valid OTP formats recognized correctly");
    }

    /**
     * Test 6: OTP format validation - invalid OTPs.
     */
    @Test
    public void testIsValidOTPFormatInvalid() {
        System.out.println("Test: Invalid OTP format validation");

        assertFalse("'123' should be invalid (too short)", generateotp.isValidOTPFormat("123"));
        assertFalse("'12345' should be invalid (too long)", generateotp.isValidOTPFormat("12345"));
        assertFalse("'abcd' should be invalid (non-numeric)", generateotp.isValidOTPFormat("abcd"));
        assertFalse("'12a4' should be invalid (contains letter)", generateotp.isValidOTPFormat("12a4"));
        assertFalse("null should be invalid", generateotp.isValidOTPFormat(null));
        assertFalse("Empty string should be invalid", generateotp.isValidOTPFormat(""));

        System.out.println("✓ Invalid OTP formats rejected correctly");
    }

    /**
     * Test 7: Generated OTPs pass validation.
     */
    @Test
    public void testGeneratedOTPsPassValidation() {
        System.out.println("Test: Generated OTPs pass format validation");

        for (int i = 0; i < 50; i++) {
            String otp = generateotp.generateOTP();
            assertTrue("Generated OTP should pass validation: " + otp,
                    generateotp.isValidOTPFormat(otp));
        }

        System.out.println("✓ All generated OTPs pass format validation");
    }

    /**
     * Test 8: OTP range validation (0000-9999).
     */
    @Test
    public void testOTPRange() {
        System.out.println("Test: OTP within valid range");

        for (int i = 0; i < 100; i++) {
            String otp = generateotp.generateOTP();
            int otpValue = Integer.parseInt(otp);

            assertTrue("OTP should be >= 0", otpValue >= 0);
            assertTrue("OTP should be <= 9999", otpValue <= 9999);
        }

        System.out.println("✓ All OTPs within valid range (0000-9999)");
    }

    /**
     * Test 9: OTP distribution (statistical test).
     */
    @Test
    public void testOTPDistribution() {
        System.out.println("Test: OTP distribution");

        int testCount = 1000;
        int[] digitCounts = new int[10]; // Count each digit 0-9

        // Count occurrences of each digit across all positions
        for (int i = 0; i < testCount; i++) {
            String otp = generateotp.generateOTP();
            for (char c : otp.toCharArray()) {
                digitCounts[c - '0']++;
            }
        }

        // Expected: each digit appears about testCount * 4 / 10 times (400)
        // Allow reasonable deviation (between 300-500)
        for (int digit = 0; digit < 10; digit++) {
            int count = digitCounts[digit];
            assertTrue("Digit " + digit + " should appear reasonably often (got " + count + ")",
                    count > 300 && count < 500);
        }

        System.out.println("✓ OTP digit distribution is reasonably random");
    }

    /**
     * Test 10: Legacy method compatibility.
     */
    @Test
    public void testLegacyMethod() {
        System.out.println("Test: Legacy getotp() method compatibility");

        generateotp generator = new generateotp();

        @SuppressWarnings("deprecation")
        String otp = generator.getotp();

        assertNotNull("Legacy method should return OTP", otp);
        assertEquals("Legacy method should return 4-digit OTP", 4, otp.length());
        assertTrue("Legacy method should return numeric OTP", otp.matches("\\d{4}"));

        System.out.println("✓ Legacy method maintains backward compatibility");
    }

    /**
     * Test 11: Concurrent OTP generation (thread safety).
     */
    @Test
    public void testConcurrentOTPGeneration() throws InterruptedException {
        System.out.println("Test: Concurrent OTP generation");

        final int threadCount = 10;
        final int otpsPerThread = 100;
        final Set<String> allOTPs = java.util.Collections.synchronizedSet(new HashSet<>());
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < otpsPerThread; j++) {
                    String otp = generateotp.generateOTP();
                    assertTrue("OTP should be valid", generateotp.isValidOTPFormat(otp));
                    allOTPs.add(otp);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Should generate mostly unique OTPs even with concurrent generation
        int totalGenerated = threadCount * otpsPerThread;
        assertTrue("Should generate mostly unique OTPs in concurrent environment",
                allOTPs.size() > totalGenerated * 0.90);

        System.out.println("✓ Concurrent OTP generation is thread-safe (" +
                allOTPs.size() + "/" + totalGenerated + " unique)");
    }

    /**
     * Test 12: OTP format edge cases.
     */
    @Test
    public void testOTPFormatEdgeCases() {
        System.out.println("Test: OTP format edge cases");

        assertFalse("Whitespace should be invalid", generateotp.isValidOTPFormat("  1234  "));
        assertFalse("With dashes should be invalid", generateotp.isValidOTPFormat("12-34"));
        assertFalse("With spaces should be invalid", generateotp.isValidOTPFormat("12 34"));
        assertFalse("With special chars should be invalid", generateotp.isValidOTPFormat("12#4"));

        System.out.println("✓ OTP format edge cases handled correctly");
    }
}
