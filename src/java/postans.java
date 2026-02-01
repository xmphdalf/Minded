import connectdb.ctodb;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet for posting comments/answers to questions.
 * Includes session validation, input sanitization, and prepared statements.
 *
 * @author Minded Team
 * @version 2.0
 */
@WebServlet(urlPatterns = {"/postans"})
public class postans extends HttpServlet {

    private static final int MAX_COMMENT_LENGTH = 2000;

    /**
     * Handles the HTTP POST method for submitting comments.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Session validation - user must be logged in
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp?error=not_logged_in");
            return;
        }

        // Get username from session (NOT from request parameter - security!)
        String username = (String) session.getAttribute("username");
        String comment = request.getParameter("comment");
        String postIdStr = request.getParameter("pid");

        // Validate comment content
        if (comment == null || comment.trim().isEmpty()) {
            response.sendRedirect(request.getHeader("referer") + "?error=empty_comment");
            return;
        }

        if (comment.length() > MAX_COMMENT_LENGTH) {
            response.sendRedirect(request.getHeader("referer") + "?error=comment_too_long");
            return;
        }

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

        try {
            // Verify post exists before adding comment
            String sqlCheckPost = "SELECT pid FROM user_post WHERE pid = ?";
            ResultSet postRs = ctodb.executeQuery(sqlCheckPost, postId);

            if (postRs == null || !postRs.next()) {
                if (postRs != null) postRs.close();
                response.sendRedirect(request.getHeader("referer") + "?error=post_not_found");
                return;
            }
            postRs.close();

            // Get user ID using prepared statement
            String sqlGetUpid = "SELECT upid FROM user_profile WHERE username = ?";
            ResultSet userRs = ctodb.executeQuery(sqlGetUpid, username);

            if (userRs == null || !userRs.next()) {
                if (userRs != null) userRs.close();
                response.sendRedirect("login.jsp?error=user_not_found");
                return;
            }

            int upid = userRs.getInt("upid");
            userRs.close();

            // Sanitize comment content (prevent XSS)
            String sanitizedComment = sanitize(comment);

            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
            dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            String formattedDate = dateFormat.format(new Date());

            // Insert comment using prepared statement
            String sqlInsert = "INSERT INTO post_ans(pid, upid, content, date) VALUES(?, ?, ?, ?)";
            int result = ctodb.executeUpdate(sqlInsert, postId, upid, sanitizedComment, formattedDate);

            if (result > 0) {
                String referer = request.getHeader("referer");
                if (referer != null) {
                    response.sendRedirect(referer);
                } else {
                    response.sendRedirect("index.jsp");
                }
            } else {
                response.sendRedirect(request.getHeader("referer") + "?error=comment_failed");
            }

        } catch (Exception e) {
            System.err.println("Error posting comment: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getHeader("referer") + "?error=server_error");
        }
    }

    /**
     * Sanitize input to prevent XSS attacks.
     */
    private String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }

    @Override
    public String getServletInfo() {
        return "Comment/Answer Posting Servlet";
    }
}
