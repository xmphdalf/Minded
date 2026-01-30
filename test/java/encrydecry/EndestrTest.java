package encrydecry;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for endestr password encryption utility.
 * Tests MD5 hashing functionality and password verification.
 *
 * @author Minded Team
 */
public class EndestrTest {

    /**
     * Test 1: MD5 hash generation produces consistent results.
     * Same input should always produce same hash.
     */
    @Test
    public void testGetMd5ConsistentHashing() {
        System.out.println("Test: MD5 hash generation consistency");

        String password = "testPassword123";
        String hash1 = endestr.getMd5(password);
        String hash2 = endestr.getMd5(password);

        assertNotNull("Hash should not be null", hash1);
        assertNotNull("Second hash should not be null", hash2);
        assertEquals("Same password should produce same hash", hash1, hash2);

        System.out.println("✓ MD5 hashing is consistent");
    }

    /**
     * Test 2: Different passwords generate different hashes.
     */
    @Test
    public void testGetMd5DifferentPasswords() {
        System.out.println("Test: Different passwords produce different hashes");

        String password1 = "password123";
        String password2 = "password456";

        String hash1 = endestr.getMd5(password1);
        String hash2 = endestr.getMd5(password2);

        assertNotNull("First hash should not be null", hash1);
        assertNotNull("Second hash should not be null", hash2);
        assertNotEquals("Different passwords should produce different hashes", hash1, hash2);

        System.out.println("✓ Different passwords produce unique hashes");
    }

    /**
     * Test 3: MD5 hash has correct format and length.
     * MD5 produces 32-character hexadecimal string.
     */
    @Test
    public void testGetMd5Format() {
        System.out.println("Test: MD5 hash format validation");

        String password = "mySecurePassword";
        String hash = endestr.getMd5(password);

        assertNotNull("Hash should not be null", hash);
        assertEquals("MD5 hash should be 32 characters", 32, hash.length());
        assertTrue("Hash should be hexadecimal",
                hash.matches("[0-9a-f]{32}"));

        System.out.println("✓ MD5 hash has correct format (32 hex characters)");
    }

    /**
     * Test 4: Null password handling.
     */
    @Test
    public void testGetMd5NullPassword() {
        System.out.println("Test: Null password handling");

        String hash = endestr.getMd5(null);
        assertNull("Null password should return null hash", hash);

        System.out.println("✓ Null password handled correctly");
    }

    /**
     * Test 5: Empty string password handling.
     */
    @Test
    public void testGetMd5EmptyPassword() {
        System.out.println("Test: Empty password handling");

        String hash = endestr.getMd5("");
        assertNull("Empty password should return null hash", hash);

        System.out.println("✓ Empty password handled correctly");
    }

    /**
     * Test 6: Password verification with correct password.
     */
    @Test
    public void testVerifyPasswordCorrect() {
        System.out.println("Test: Password verification with correct password");

        String plainPassword = "myPassword123";
        String hashedPassword = endestr.getMd5(plainPassword);

        boolean result = endestr.verifyPassword(plainPassword, hashedPassword);
        assertTrue("Correct password should verify successfully", result);

        System.out.println("✓ Password verification succeeds with correct password");
    }

    /**
     * Test 7: Password verification with incorrect password.
     */
    @Test
    public void testVerifyPasswordIncorrect() {
        System.out.println("Test: Password verification with incorrect password");

        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hashedPassword = endestr.getMd5(correctPassword);

        boolean result = endestr.verifyPassword(wrongPassword, hashedPassword);
        assertFalse("Incorrect password should not verify", result);

        System.out.println("✓ Password verification fails with incorrect password");
    }

    /**
     * Test 8: Password verification with null values.
     */
    @Test
    public void testVerifyPasswordNullHandling() {
        System.out.println("Test: Password verification null handling");

        String hashedPassword = endestr.getMd5("password");

        assertFalse("Null plain password should return false",
                endestr.verifyPassword(null, hashedPassword));
        assertFalse("Null hashed password should return false",
                endestr.verifyPassword("password", null));
        assertFalse("Both null should return false",
                endestr.verifyPassword(null, null));

        System.out.println("✓ Password verification handles null values correctly");
    }

    /**
     * Test 9: Case sensitivity in passwords.
     */
    @Test
    public void testGetMd5CaseSensitivity() {
        System.out.println("Test: Password case sensitivity");

        String lowercase = "password";
        String uppercase = "PASSWORD";
        String mixed = "PaSsWoRd";

        String hash1 = endestr.getMd5(lowercase);
        String hash2 = endestr.getMd5(uppercase);
        String hash3 = endestr.getMd5(mixed);

        assertNotEquals("Lowercase and uppercase should produce different hashes",
                hash1, hash2);
        assertNotEquals("Mixed case should be different from lowercase",
                hash1, hash3);
        assertNotEquals("Mixed case should be different from uppercase",
                hash2, hash3);

        System.out.println("✓ Password hashing is case-sensitive");
    }

    /**
     * Test 10: Special characters in passwords.
     */
    @Test
    public void testGetMd5SpecialCharacters() {
        System.out.println("Test: Special characters in passwords");

        String password = "p@ssw0rd!#$%^&*()";
        String hash = endestr.getMd5(password);

        assertNotNull("Password with special characters should hash successfully", hash);
        assertEquals("Hash should be 32 characters", 32, hash.length());

        // Verify it can be verified
        assertTrue("Password with special characters should verify",
                endestr.verifyPassword(password, hash));

        System.out.println("✓ Special characters handled correctly");
    }

    /**
     * Test 11: Unicode characters in passwords.
     */
    @Test
    public void testGetMd5UnicodeCharacters() {
        System.out.println("Test: Unicode characters in passwords");

        String password = "пароль密码パスワード"; // Russian, Chinese, Japanese
        String hash = endestr.getMd5(password);

        assertNotNull("Password with Unicode should hash successfully", hash);
        assertTrue("Password with Unicode should verify",
                endestr.verifyPassword(password, hash));

        System.out.println("✓ Unicode characters handled correctly");
    }

    /**
     * Test 12: Legacy method compatibility.
     */
    @Test
    public void testEncdecmeLegacyMethod() {
        System.out.println("Test: Legacy encdecme method compatibility");

        endestr instance = new endestr();
        String password = "legacyPassword";

        @SuppressWarnings("deprecation")
        String legacyHash = instance.encdecme(password);
        String modernHash = endestr.getMd5(password);

        assertNotNull("Legacy method should return hash", legacyHash);
        assertEquals("Legacy method should produce same hash as modern method",
                modernHash, legacyHash);

        System.out.println("✓ Legacy method maintains backward compatibility");
    }

    /**
     * Test 13: Known MD5 hash verification.
     * Tests against known MD5 values to ensure correctness.
     */
    @Test
    public void testGetMd5KnownValues() {
        System.out.println("Test: Known MD5 hash values");

        // Known MD5 values from online MD5 generators
        assertEquals("MD5 of 'password' should be known value",
                "5f4dcc3b5aa765d61d8327deb882cf99",
                endestr.getMd5("password"));

        assertEquals("MD5 of 'test' should be known value",
                "098f6bcd4621d373cade4e832627b4f6",
                endestr.getMd5("test"));

        System.out.println("✓ MD5 hashing produces correct known values");
    }

    /**
     * Test 14: Very long password handling.
     */
    @Test
    public void testGetMd5LongPassword() {
        System.out.println("Test: Very long password handling");

        // Create a 1000-character password
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append((char) ('a' + (i % 26)));
        }
        String longPassword = sb.toString();

        String hash = endestr.getMd5(longPassword);
        assertNotNull("Long password should hash successfully", hash);
        assertEquals("Hash should still be 32 characters", 32, hash.length());

        System.out.println("✓ Long passwords handled correctly");
    }

    /**
     * Test 15: Whitespace in passwords.
     */
    @Test
    public void testGetMd5Whitespace() {
        System.out.println("Test: Whitespace in passwords");

        String password1 = "password";
        String password2 = " password";  // Leading space
        String password3 = "password ";  // Trailing space
        String password4 = "pass word";  // Space in middle

        String hash1 = endestr.getMd5(password1);
        String hash2 = endestr.getMd5(password2);
        String hash3 = endestr.getMd5(password3);
        String hash4 = endestr.getMd5(password4);

        // All should produce different hashes
        assertNotEquals("Leading space should produce different hash", hash1, hash2);
        assertNotEquals("Trailing space should produce different hash", hash1, hash3);
        assertNotEquals("Internal space should produce different hash", hash1, hash4);

        System.out.println("✓ Whitespace in passwords handled correctly");
    }
}
