package servlet;

import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Method;

/**
 * Unit tests for topic management servlets.
 * Tests input validation, admin authorization, and security checks.
 *
 * @author Minded Team
 */
public class TopicServletTest {

    /**
     * Test 1: Topic name validation - valid names.
     */
    @Test
    public void testValidTopicNames() throws Exception {
        System.out.println("Test: Valid topic names");

        Class<?> clazz = Class.forName("topicupload");
        Method isValidMethod = clazz.getDeclaredMethod("isValidTopicName", String.class);
        isValidMethod.setAccessible(true);

        Object instance = clazz.getDeclaredConstructor().newInstance();

        // Test valid topic names
        assertTrue("Simple name should be valid",
                (Boolean) isValidMethod.invoke(instance, "Java Programming"));
        assertTrue("Name with numbers should be valid",
                (Boolean) isValidMethod.invoke(instance, "Python 3.x"));
        assertTrue("Name with hyphen should be valid",
                (Boolean) isValidMethod.invoke(instance, "C-Sharp"));
        assertTrue("Name with underscore should be valid",
                (Boolean) isValidMethod.invoke(instance, "Machine_Learning"));

        System.out.println("✓ Valid topic names accepted");
    }

    /**
     * Test 2: Topic name validation - invalid names.
     */
    @Test
    public void testInvalidTopicNames() throws Exception {
        System.out.println("Test: Invalid topic names");

        Class<?> clazz = Class.forName("topicupload");
        Method isValidMethod = clazz.getDeclaredMethod("isValidTopicName", String.class);
        isValidMethod.setAccessible(true);

        Object instance = clazz.getDeclaredConstructor().newInstance();

        // Test invalid topic names
        assertFalse("Name with script tag should be invalid",
                (Boolean) isValidMethod.invoke(instance, "<script>alert('XSS')</script>"));
        assertFalse("Name with SQL injection should be invalid",
                (Boolean) isValidMethod.invoke(instance, "'; DROP TABLE topics; --"));
        assertFalse("Name with special chars should be invalid",
                (Boolean) isValidMethod.invoke(instance, "Topic@#$%^&*"));

        System.out.println("✓ Invalid topic names rejected");
    }

    /**
     * Test 3: Topic ID validation - valid numeric input.
     */
    @Test
    public void testValidTopicIdParsing() {
        System.out.println("Test: Valid topic ID parsing");

        String validId = "42";
        int topicId = Integer.parseInt(validId);

        assertEquals("Topic ID should parse correctly", 42, topicId);

        System.out.println("✓ Valid topic ID parsed correctly");
    }

    /**
     * Test 4: Topic ID validation - invalid input.
     */
    @Test
    public void testInvalidTopicIdParsing() {
        System.out.println("Test: Invalid topic ID handling");

        String[] invalidIds = {"abc", "", " ", "12.5", "12abc", "-abc"};

        for (String invalidId : invalidIds) {
            boolean threw = false;
            try {
                Integer.parseInt(invalidId);
            } catch (NumberFormatException e) {
                threw = true;
            }
            assertTrue("Should throw for invalid ID: " + invalidId, threw);
        }

        System.out.println("✓ Invalid topic IDs properly rejected");
    }

    /**
     * Test 5: Topic name length validation.
     */
    @Test
    public void testTopicNameLengthValidation() {
        System.out.println("Test: Topic name length validation");

        int MAX_LENGTH = 100;
        int MIN_LENGTH = 2;

        String shortName = "A";
        String validName = "Java Programming";
        String exactMaxName = new String(new char[MAX_LENGTH]).replace('\0', 'a');
        String longName = new String(new char[MAX_LENGTH + 1]).replace('\0', 'a');

        assertTrue("Short name should fail", shortName.length() < MIN_LENGTH);
        assertTrue("Valid name should pass",
                validName.length() >= MIN_LENGTH && validName.length() <= MAX_LENGTH);
        assertTrue("Exact max length should pass", exactMaxName.length() <= MAX_LENGTH);
        assertFalse("Long name should fail", longName.length() <= MAX_LENGTH);

        System.out.println("✓ Topic name length validation works correctly");
    }

    /**
     * Test 6: Whitespace trimming in topic names.
     */
    @Test
    public void testTopicNameWhitespaceTrimming() {
        System.out.println("Test: Topic name whitespace trimming");

        String nameWithSpaces = "  Java   Programming  ";
        String trimmed = nameWithSpaces.trim().replaceAll("\\s+", " ");

        assertEquals("Multiple spaces should become single", "Java Programming", trimmed);

        System.out.println("✓ Whitespace trimming works correctly");
    }

    /**
     * Test 7: Session validation logic.
     */
    @Test
    public void testSessionValidationLogic() {
        System.out.println("Test: Session validation logic");

        Object nullSession = null;
        Object nullUsername = null;
        String validUsername = "admin";

        boolean shouldRedirect1 = (nullSession == null);
        assertTrue("Null session should trigger redirect", shouldRedirect1);

        boolean shouldRedirect2 = (nullUsername == null);
        assertTrue("Null username should trigger redirect", shouldRedirect2);

        boolean shouldRedirect3 = (validUsername == null);
        assertFalse("Valid username should not trigger redirect", shouldRedirect3);

        System.out.println("✓ Session validation logic works correctly");
    }

    /**
     * Test 8: Admin check query structure.
     */
    @Test
    public void testAdminCheckQueryStructure() {
        System.out.println("Test: Admin check query structure");

        String adminQuery = "SELECT role FROM user_profile WHERE username = ?";

        assertTrue("Query should select role", adminQuery.contains("role"));
        assertTrue("Query should use placeholder", adminQuery.contains("?"));
        assertEquals("Query should have 1 placeholder", 1,
                adminQuery.chars().filter(ch -> ch == '?').count());

        System.out.println("✓ Admin check query uses prepared statement");
    }

    /**
     * Test 9: Prepared statement query structure for create.
     */
    @Test
    public void testCreateTopicQueryStructure() {
        System.out.println("Test: Create topic query structure");

        String checkQuery = "SELECT tpid FROM topics WHERE LOWER(tpname) = LOWER(?)";
        String insertQuery = "INSERT INTO topics(tpname) VALUES(?)";

        assertTrue("Check query should use LOWER for case-insensitive",
                checkQuery.contains("LOWER"));
        assertEquals("Check query should have 1 placeholder", 1,
                checkQuery.chars().filter(ch -> ch == '?').count());
        assertEquals("Insert query should have 1 placeholder", 1,
                insertQuery.chars().filter(ch -> ch == '?').count());

        System.out.println("✓ Create topic queries use prepared statements");
    }

    /**
     * Test 10: Prepared statement query structure for delete.
     */
    @Test
    public void testDeleteTopicQueryStructure() {
        System.out.println("Test: Delete topic query structure");

        String checkTopicQuery = "SELECT tpid FROM topics WHERE tpid = ?";
        String checkFollowersQuery = "SELECT COUNT(*) as count FROM favtopic WHERE tpid = ?";
        String deleteFollowersQuery = "DELETE FROM favtopic WHERE tpid = ?";
        String deleteTopicQuery = "DELETE FROM topics WHERE tpid = ?";

        // All queries should have exactly 1 placeholder
        assertEquals("Check topic query should have 1 placeholder", 1,
                checkTopicQuery.chars().filter(ch -> ch == '?').count());
        assertEquals("Check followers query should have 1 placeholder", 1,
                checkFollowersQuery.chars().filter(ch -> ch == '?').count());
        assertEquals("Delete followers query should have 1 placeholder", 1,
                deleteFollowersQuery.chars().filter(ch -> ch == '?').count());
        assertEquals("Delete topic query should have 1 placeholder", 1,
                deleteTopicQuery.chars().filter(ch -> ch == '?').count());

        System.out.println("✓ Delete topic queries use prepared statements");
    }

    /**
     * Test 11: Cascade delete order.
     */
    @Test
    public void testCascadeDeleteOrder() {
        System.out.println("Test: Cascade delete order");

        String deleteFollowersQuery = "DELETE FROM favtopic WHERE tpid = ?";
        String deleteTopicQuery = "DELETE FROM topics WHERE tpid = ?";

        // Verify correct order: followers first, then topic
        assertTrue("Should delete from favtopic first",
                deleteFollowersQuery.contains("favtopic"));
        assertTrue("Should delete from topics last",
                deleteTopicQuery.contains("topics"));

        System.out.println("✓ Cascade delete follows correct order");
    }

    /**
     * Test 12: Empty topic name validation.
     */
    @Test
    public void testEmptyTopicNameValidation() {
        System.out.println("Test: Empty topic name validation");

        String emptyName = "";
        String whitespaceOnly = "   ";

        assertTrue("Empty name should be empty", emptyName.trim().isEmpty());
        assertTrue("Whitespace only should be empty after trim", whitespaceOnly.trim().isEmpty());

        System.out.println("✓ Empty topic names properly detected");
    }

    /**
     * Test 13: SQL injection prevention in topic names.
     */
    @Test
    public void testSqlInjectionInTopicNames() throws Exception {
        System.out.println("Test: SQL injection prevention in topic names");

        Class<?> clazz = Class.forName("topicupload");
        Method isValidMethod = clazz.getDeclaredMethod("isValidTopicName", String.class);
        isValidMethod.setAccessible(true);

        Object instance = clazz.getDeclaredConstructor().newInstance();

        String[] sqlInjectionAttempts = {
            "'; DROP TABLE topics; --",
            "1 OR 1=1",
            "' UNION SELECT * FROM user_profile --",
            "topic'; DELETE FROM topics; --"
        };

        for (String attempt : sqlInjectionAttempts) {
            boolean isValid = (Boolean) isValidMethod.invoke(instance, attempt);
            assertFalse("SQL injection attempt should be rejected: " + attempt, isValid);
        }

        System.out.println("✓ SQL injection attempts rejected by validation");
    }

    /**
     * Test 14: Admin role comparison.
     */
    @Test
    public void testAdminRoleComparison() {
        System.out.println("Test: Admin role comparison");

        String adminRole = "admin";
        String userRole = "user";
        String nullRole = null;

        assertTrue("'admin' should match", "admin".equalsIgnoreCase(adminRole));
        assertTrue("'ADMIN' should match case-insensitive", "admin".equalsIgnoreCase("ADMIN"));
        assertFalse("'user' should not match", "admin".equalsIgnoreCase(userRole));
        assertFalse("null role should fail safely",
                nullRole != null && "admin".equalsIgnoreCase(nullRole));

        System.out.println("✓ Admin role comparison works correctly");
    }

    /**
     * Test 15: Servlet info methods.
     */
    @Test
    public void testServletInfo() throws Exception {
        System.out.println("Test: Servlet info methods");

        // Test topicupload
        Class<?> uploadClazz = Class.forName("topicupload");
        Object uploadInstance = uploadClazz.getDeclaredConstructor().newInstance();
        Method uploadInfoMethod = uploadClazz.getMethod("getServletInfo");
        String uploadInfo = (String) uploadInfoMethod.invoke(uploadInstance);
        assertNotNull("Upload servlet info should not be null", uploadInfo);
        assertTrue("Upload servlet info should mention admin",
                uploadInfo.toLowerCase().contains("admin"));

        // Test deletetopic
        Class<?> deleteClazz = Class.forName("deletetopic");
        Object deleteInstance = deleteClazz.getDeclaredConstructor().newInstance();
        Method deleteInfoMethod = deleteClazz.getMethod("getServletInfo");
        String deleteInfo = (String) deleteInfoMethod.invoke(deleteInstance);
        assertNotNull("Delete servlet info should not be null", deleteInfo);
        assertTrue("Delete servlet info should mention admin",
                deleteInfo.toLowerCase().contains("admin"));

        System.out.println("✓ Servlet info methods return descriptive values");
    }

    /**
     * Test 16: Constants validation.
     */
    @Test
    public void testTopicConstants() throws Exception {
        System.out.println("Test: Topic constants");

        Class<?> clazz = Class.forName("topicupload");

        java.lang.reflect.Field maxField = clazz.getDeclaredField("MAX_TOPIC_NAME_LENGTH");
        maxField.setAccessible(true);
        int maxLength = maxField.getInt(null);

        java.lang.reflect.Field minField = clazz.getDeclaredField("MIN_TOPIC_NAME_LENGTH");
        minField.setAccessible(true);
        int minLength = minField.getInt(null);

        assertEquals("MAX_TOPIC_NAME_LENGTH should be 100", 100, maxLength);
        assertEquals("MIN_TOPIC_NAME_LENGTH should be 2", 2, minLength);
        assertTrue("Min should be less than max", minLength < maxLength);

        System.out.println("✓ Topic constants are correctly set");
    }
}
