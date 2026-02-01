import connectdb.ctodb;
import java.io.IOException;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet for handling upvotes and downvotes on posts.
 * Includes session validation, duplicate vote prevention, and prepared statements.
 *
 * @author Minded Team
 * @version 2.0
 */
@WebServlet(urlPatterns = {"/vote"})
public class vote extends HttpServlet {

    /**
     * Handles the HTTP POST method for voting.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Session validation - user must be logged in to vote
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp?error=not_logged_in");
            return;
        }

        String username = (String) session.getAttribute("username");
        String postIdStr = request.getParameter("pid");
        String voteType = request.getParameter("type"); // "up" or "down"

        // Validate post ID
        if (postIdStr == null || postIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getHeader("referer") + "?error=invalid_post");
            return;
        }

        int postId;
        try {
            postId = Integer.parseInt(postIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getHeader("referer") + "?error=invalid_post_id");
            return;
        }

        // Validate vote type
        if (voteType == null || (!voteType.equals("up") && !voteType.equals("down"))) {
            response.sendRedirect(request.getHeader("referer") + "?error=invalid_vote_type");
            return;
        }

        try {
            // Get user ID
            String sqlGetUpid = "SELECT upid FROM user_profile WHERE username = ?";
            ResultSet userRs = ctodb.executeQuery(sqlGetUpid, username);

            if (userRs == null || !userRs.next()) {
                if (userRs != null) userRs.close();
                response.sendRedirect("login.jsp?error=user_not_found");
                return;
            }

            int upid = userRs.getInt("upid");
            userRs.close();

            // Verify post exists
            String sqlCheckPost = "SELECT pid, upid FROM user_post WHERE pid = ?";
            ResultSet postRs = ctodb.executeQuery(sqlCheckPost, postId);

            if (postRs == null || !postRs.next()) {
                if (postRs != null) postRs.close();
                response.sendRedirect(request.getHeader("referer") + "?error=post_not_found");
                return;
            }

            int postOwnerId = postRs.getInt("upid");
            postRs.close();

            // Prevent self-voting
            if (upid == postOwnerId) {
                response.sendRedirect(request.getHeader("referer") + "?error=cannot_vote_own_post");
                return;
            }

            // Check if user already voted on this post
            String sqlCheckVote = "SELECT vote_type FROM post_votes WHERE pid = ? AND upid = ?";
            ResultSet voteRs = ctodb.executeQuery(sqlCheckVote, postId, upid);

            String existingVote = null;
            if (voteRs != null && voteRs.next()) {
                existingVote = voteRs.getString("vote_type");
                voteRs.close();
            } else if (voteRs != null) {
                voteRs.close();
            }

            if (existingVote != null) {
                if (existingVote.equals(voteType)) {
                    // Remove vote (toggle off)
                    String sqlDeleteVote = "DELETE FROM post_votes WHERE pid = ? AND upid = ?";
                    ctodb.executeUpdate(sqlDeleteVote, postId, upid);

                    // Decrement vote count
                    String voteColumn = voteType.equals("up") ? "upvote" : "downvote";
                    String sqlDecrement = "UPDATE user_post SET " + voteColumn + " = " + voteColumn + " - 1 WHERE pid = ?";
                    ctodb.executeUpdate(sqlDecrement, postId);
                } else {
                    // Change vote type
                    String sqlUpdateVote = "UPDATE post_votes SET vote_type = ? WHERE pid = ? AND upid = ?";
                    ctodb.executeUpdate(sqlUpdateVote, voteType, postId, upid);

                    // Adjust vote counts
                    if (voteType.equals("up")) {
                        String sqlAdjust = "UPDATE user_post SET upvote = upvote + 1, downvote = downvote - 1 WHERE pid = ?";
                        ctodb.executeUpdate(sqlAdjust, postId);
                    } else {
                        String sqlAdjust = "UPDATE user_post SET downvote = downvote + 1, upvote = upvote - 1 WHERE pid = ?";
                        ctodb.executeUpdate(sqlAdjust, postId);
                    }
                }
            } else {
                // New vote
                String sqlInsertVote = "INSERT INTO post_votes(pid, upid, vote_type) VALUES(?, ?, ?)";
                ctodb.executeUpdate(sqlInsertVote, postId, upid, voteType);

                // Increment vote count
                String voteColumn = voteType.equals("up") ? "upvote" : "downvote";
                String sqlIncrement = "UPDATE user_post SET " + voteColumn + " = " + voteColumn + " + 1 WHERE pid = ?";
                ctodb.executeUpdate(sqlIncrement, postId);
            }

            String referer = request.getHeader("referer");
            if (referer != null) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect("index.jsp");
            }

        } catch (Exception e) {
            System.err.println("Error processing vote: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getHeader("referer") + "?error=server_error");
        }
    }

    /**
     * Handles GET requests - redirect to POST for legacy compatibility.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Session validation
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp?error=not_logged_in");
            return;
        }

        // Check for upvote parameter (legacy support)
        String upvoteId = request.getParameter("idup");
        String downvoteId = request.getParameter("iddw");

        if (upvoteId != null && !upvoteId.trim().isEmpty()) {
            request.setAttribute("pid", upvoteId);
            request.setAttribute("type", "up");
            // Forward to POST handler
            processVoteGet(request, response, upvoteId, "up");
        } else if (downvoteId != null && !downvoteId.trim().isEmpty()) {
            request.setAttribute("pid", downvoteId);
            request.setAttribute("type", "down");
            processVoteGet(request, response, downvoteId, "down");
        } else {
            response.sendRedirect(request.getHeader("referer") + "?error=invalid_request");
        }
    }

    /**
     * Process vote from GET request (legacy support).
     */
    private void processVoteGet(HttpServletRequest request, HttpServletResponse response,
                                String postIdStr, String voteType) throws IOException {

        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("username");

        int postId;
        try {
            postId = Integer.parseInt(postIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getHeader("referer") + "?error=invalid_post_id");
            return;
        }

        try {
            // Get user ID
            String sqlGetUpid = "SELECT upid FROM user_profile WHERE username = ?";
            ResultSet userRs = ctodb.executeQuery(sqlGetUpid, username);

            if (userRs == null || !userRs.next()) {
                if (userRs != null) userRs.close();
                response.sendRedirect("login.jsp?error=user_not_found");
                return;
            }

            int upid = userRs.getInt("upid");
            userRs.close();

            // Simple increment for legacy compatibility (no duplicate check)
            String voteColumn = voteType.equals("up") ? "upvote" : "downvote";
            String sqlUpdate = "UPDATE user_post SET " + voteColumn + " = " + voteColumn + " + 1 WHERE pid = ?";
            int result = ctodb.executeUpdate(sqlUpdate, postId);

            if (result > 0) {
                String referer = request.getHeader("referer");
                if (referer != null) {
                    response.sendRedirect(referer);
                } else {
                    response.sendRedirect("index.jsp");
                }
            } else {
                response.sendRedirect(request.getHeader("referer") + "?error=vote_failed");
            }

        } catch (Exception e) {
            System.err.println("Error processing vote: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getHeader("referer") + "?error=server_error");
        }
    }

    @Override
    public String getServletInfo() {
        return "Post Voting Servlet";
    }
}
