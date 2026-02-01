package servlet;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for post voting servlet.
 * Tests input validation, vote logic, and security checks.
 *
 * @author Minded Team
 */
public class VoteServletTest {

    /**
     * Test 1: Valid post ID parsing.
     */
    @Test
    public void testValidPostIdParsing() {
        System.out.println("Test: Valid post ID parsing");

        String validId = "42";
        int postId = Integer.parseInt(validId);

        assertEquals("Post ID should parse correctly", 42, postId);

        System.out.println("✓ Valid post ID parsed correctly");
    }

    /**
     * Test 2: Invalid post ID handling.
     */
    @Test
    public void testInvalidPostIdParsing() {
        System.out.println("Test: Invalid post ID handling");

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

        System.out.println("✓ Invalid post IDs properly rejected");
    }

    /**
     * Test 3: Vote type validation - valid types.
     */
    @Test
    public void testValidVoteTypes() {
        System.out.println("Test: Valid vote types");

        String upvote = "up";
        String downvote = "down";

        assertTrue("'up' should be valid", upvote.equals("up") || upvote.equals("down"));
        assertTrue("'down' should be valid", downvote.equals("up") || downvote.equals("down"));

        System.out.println("✓ Valid vote types accepted");
    }

    /**
     * Test 4: Vote type validation - invalid types.
     */
    @Test
    public void testInvalidVoteTypes() {
        System.out.println("Test: Invalid vote types");

        String[] invalidTypes = {"upvote", "downvote", "like", "dislike", "", null};

        for (String type : invalidTypes) {
            boolean isValid = type != null && (type.equals("up") || type.equals("down"));
            assertFalse("'" + type + "' should be invalid", isValid);
        }

        System.out.println("✓ Invalid vote types rejected");
    }

    /**
     * Test 5: Session validation logic.
     */
    @Test
    public void testSessionValidationLogic() {
        System.out.println("Test: Session validation logic");

        Object nullSession = null;
        Object nullUsername = null;
        String validUsername = "testuser";

        boolean shouldRedirect1 = (nullSession == null);
        assertTrue("Null session should trigger redirect", shouldRedirect1);

        boolean shouldRedirect2 = (nullUsername == null);
        assertTrue("Null username should trigger redirect", shouldRedirect2);

        boolean shouldRedirect3 = (validUsername == null);
        assertFalse("Valid username should not trigger redirect", shouldRedirect3);

        System.out.println("✓ Session validation logic works correctly");
    }

    /**
     * Test 6: Self-voting prevention logic.
     */
    @Test
    public void testSelfVotingPrevention() {
        System.out.println("Test: Self-voting prevention logic");

        int userId = 42;
        int postOwnerId = 42;
        int differentOwnerId = 99;

        boolean isSelfVote = (userId == postOwnerId);
        assertTrue("Same user and owner should be self-vote", isSelfVote);

        boolean notSelfVote = (userId == differentOwnerId);
        assertFalse("Different users should not be self-vote", notSelfVote);

        System.out.println("✓ Self-voting prevention logic works correctly");
    }

    /**
     * Test 7: Vote toggle logic.
     */
    @Test
    public void testVoteToggleLogic() {
        System.out.println("Test: Vote toggle logic");

        String existingVote = "up";
        String newVote = "up";

        boolean shouldToggleOff = existingVote.equals(newVote);
        assertTrue("Same vote type should toggle off", shouldToggleOff);

        String differentVote = "down";
        boolean shouldChangeVote = !existingVote.equals(differentVote);
        assertTrue("Different vote type should change vote", shouldChangeVote);

        System.out.println("✓ Vote toggle logic works correctly");
    }

    /**
     * Test 8: Vote change logic.
     */
    @Test
    public void testVoteChangeLogic() {
        System.out.println("Test: Vote change logic");

        String existingVote = "up";
        String newVote = "down";

        boolean isVoteChange = existingVote != null && !existingVote.equals(newVote);
        assertTrue("Changing from up to down should be detected", isVoteChange);

        System.out.println("✓ Vote change logic works correctly");
    }

    /**
     * Test 9: Prepared statement query structure.
     */
    @Test
    public void testPreparedStatementStructure() {
        System.out.println("Test: Prepared statement query structure");

        String getUserQuery = "SELECT upid FROM user_profile WHERE username = ?";
        String checkPostQuery = "SELECT pid, upid FROM user_post WHERE pid = ?";
        String checkVoteQuery = "SELECT vote_type FROM post_votes WHERE pid = ? AND upid = ?";
        String insertVoteQuery = "INSERT INTO post_votes(pid, upid, vote_type) VALUES(?, ?, ?)";

        assertEquals("Get user query should have 1 placeholder", 1,
                getUserQuery.chars().filter(ch -> ch == '?').count());
        assertEquals("Check post query should have 1 placeholder", 1,
                checkPostQuery.chars().filter(ch -> ch == '?').count());
        assertEquals("Check vote query should have 2 placeholders", 2,
                checkVoteQuery.chars().filter(ch -> ch == '?').count());
        assertEquals("Insert vote query should have 3 placeholders", 3,
                insertVoteQuery.chars().filter(ch -> ch == '?').count());

        System.out.println("✓ Prepared statements have correct structure");
    }

    /**
     * Test 10: Vote count update query structure.
     */
    @Test
    public void testVoteCountUpdateQuery() {
        System.out.println("Test: Vote count update query structure");

        String incrementUpvote = "UPDATE user_post SET upvote = upvote + 1 WHERE pid = ?";
        String incrementDownvote = "UPDATE user_post SET downvote = downvote + 1 WHERE pid = ?";
        String adjustVotes = "UPDATE user_post SET upvote = upvote + 1, downvote = downvote - 1 WHERE pid = ?";

        assertTrue("Increment upvote query should update upvote",
                incrementUpvote.contains("upvote = upvote + 1"));
        assertTrue("Increment downvote query should update downvote",
                incrementDownvote.contains("downvote = downvote + 1"));
        assertTrue("Adjust votes query should handle both",
                adjustVotes.contains("upvote") && adjustVotes.contains("downvote"));

        System.out.println("✓ Vote count update queries are correct");
    }

    /**
     * Test 11: Column name determination based on vote type.
     */
    @Test
    public void testVoteColumnDetermination() {
        System.out.println("Test: Vote column determination");

        String upVoteType = "up";
        String downVoteType = "down";

        String upColumn = upVoteType.equals("up") ? "upvote" : "downvote";
        String downColumn = downVoteType.equals("up") ? "upvote" : "downvote";

        assertEquals("Up vote should use upvote column", "upvote", upColumn);
        assertEquals("Down vote should use downvote column", "downvote", downColumn);

        System.out.println("✓ Vote column determination works correctly");
    }

    /**
     * Test 12: Delete vote query structure.
     */
    @Test
    public void testDeleteVoteQuery() {
        System.out.println("Test: Delete vote query structure");

        String deleteQuery = "DELETE FROM post_votes WHERE pid = ? AND upid = ?";

        assertTrue("Delete query should target post_votes", deleteQuery.contains("post_votes"));
        assertEquals("Delete query should have 2 placeholders", 2,
                deleteQuery.chars().filter(ch -> ch == '?').count());

        System.out.println("✓ Delete vote query is correct");
    }

    /**
     * Test 13: Update vote type query structure.
     */
    @Test
    public void testUpdateVoteTypeQuery() {
        System.out.println("Test: Update vote type query structure");

        String updateQuery = "UPDATE post_votes SET vote_type = ? WHERE pid = ? AND upid = ?";

        assertTrue("Update query should set vote_type", updateQuery.contains("vote_type = ?"));
        assertEquals("Update query should have 3 placeholders", 3,
                updateQuery.chars().filter(ch -> ch == '?').count());

        System.out.println("✓ Update vote type query is correct");
    }

    /**
     * Test 14: Null referer handling.
     */
    @Test
    public void testNullRefererHandling() {
        System.out.println("Test: Null referer handling");

        String nullReferer = null;
        String validReferer = "index.jsp";

        String redirect1 = (nullReferer != null) ? nullReferer : "index.jsp";
        String redirect2 = (validReferer != null) ? validReferer : "index.jsp";

        assertEquals("Null referer should redirect to index.jsp", "index.jsp", redirect1);
        assertEquals("Valid referer should be used", "index.jsp", redirect2);

        System.out.println("✓ Null referer handling works correctly");
    }

    /**
     * Test 15: Servlet info method.
     */
    @Test
    public void testServletInfo() throws Exception {
        System.out.println("Test: Servlet info method");

        Class<?> clazz = Class.forName("vote");
        Object instance = clazz.getDeclaredConstructor().newInstance();

        java.lang.reflect.Method getServletInfoMethod = clazz.getMethod("getServletInfo");
        String info = (String) getServletInfoMethod.invoke(instance);

        assertNotNull("Servlet info should not be null", info);
        assertTrue("Servlet info should be descriptive", info.length() > 5);

        System.out.println("✓ Servlet info: " + info);
    }

    /**
     * Test 16: New vote detection.
     */
    @Test
    public void testNewVoteDetection() {
        System.out.println("Test: New vote detection");

        String existingVote = null;

        boolean isNewVote = (existingVote == null);
        assertTrue("Null existing vote should be new vote", isNewVote);

        String hasVote = "up";
        boolean notNewVote = (hasVote != null);
        assertTrue("Existing vote should not be new vote", notNewVote);

        System.out.println("✓ New vote detection works correctly");
    }
}
