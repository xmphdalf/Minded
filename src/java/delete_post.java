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
 * Servlet for deleting user posts.
 * Includes ownership verification and cascade deletion of comments.
 *
 * @author Minded Team
 * @version 2.0
 */
@WebServlet(urlPatterns = {"/delete_post"})
public class delete_post extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Session validation - user must be logged in
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp?error=not_logged_in");
            return;
        }

        String username = (String) session.getAttribute("username");
        String postId = request.getParameter("hdnpiddt");

        // Validate post ID
        if (postId == null || postId.trim().isEmpty()) {
            response.sendRedirect("profile.jsp?error=invalid_post");
            return;
        }

        // Validate post ID is numeric
        int pid;
        try {
            pid = Integer.parseInt(postId);
        } catch (NumberFormatException e) {
            response.sendRedirect("profile.jsp?error=invalid_post_id");
            return;
        }

        try {
            // Authorization check - verify user owns this post
            String sqlCheckOwnership = "SELECT up.pid FROM user_post up " +
                    "INNER JOIN user_profile u ON up.upid = u.upid " +
                    "WHERE up.pid = ? AND u.username = ?";
            ResultSet ownerRs = ctodb.executeQuery(sqlCheckOwnership, pid, username);

            if (ownerRs == null || !ownerRs.next()) {
                // User doesn't own this post
                if (ownerRs != null) ownerRs.close();
                response.sendRedirect("profile.jsp?error=not_authorized");
                return;
            }
            ownerRs.close();

            // Check if post has comments
            String sqlCheckComments = "SELECT COUNT(*) as comment_count FROM post_ans WHERE pid = ?";
            ResultSet commentRs = ctodb.executeQuery(sqlCheckComments, pid);

            int commentCount = 0;
            if (commentRs != null && commentRs.next()) {
                commentCount = commentRs.getInt("comment_count");
                commentRs.close();
            }

            // Delete comments first (cascade delete)
            if (commentCount > 0) {
                String sqlDeleteComments = "DELETE FROM post_ans WHERE pid = ?";
                ctodb.executeUpdate(sqlDeleteComments, pid);
            }

            // Delete the post
            String sqlDeletePost = "DELETE FROM user_post WHERE pid = ?";
            int result = ctodb.executeUpdate(sqlDeletePost, pid);

            if (result > 0) {
                response.sendRedirect("profile.jsp?success=post_deleted");
            } else {
                response.sendRedirect("profile.jsp?error=delete_failed");
            }

        } catch (Exception e) {
            System.err.println("Error deleting post: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("profile.jsp?error=server_error");
        }
    }

    @Override
    public String getServletInfo() {
        return "Post Deletion Servlet with Authorization";
    }
}
