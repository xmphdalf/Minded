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
 * Servlet for deleting topics (Admin only).
 * Includes admin authentication, cascade deletion, and prepared statements.
 *
 * @author Minded Team
 * @version 2.0
 */
@WebServlet(urlPatterns = {"/deletetopic"})
public class deletetopic extends HttpServlet {

    /**
     * Handles the HTTP POST method for deleting topics.
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

        // Admin authorization check
        String username = (String) session.getAttribute("username");
        if (!isAdmin(username)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        String topicIdStr = request.getParameter("hidid");

        // Validate topic ID
        if (topicIdStr == null || topicIdStr.trim().isEmpty()) {
            response.sendRedirect("admin5/addtopic.jsp?error=invalid_topic");
            return;
        }

        int topicId;
        try {
            topicId = Integer.parseInt(topicIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("admin5/addtopic.jsp?error=invalid_topic_id");
            return;
        }

        try {
            // Verify topic exists
            String sqlCheckTopic = "SELECT tpid FROM topics WHERE tpid = ?";
            ResultSet topicRs = ctodb.executeQuery(sqlCheckTopic, topicId);

            if (topicRs == null || !topicRs.next()) {
                if (topicRs != null) topicRs.close();
                response.sendRedirect("admin5/addtopic.jsp?error=topic_not_found");
                return;
            }
            topicRs.close();

            // Check if topic has followers (cascade delete)
            String sqlCheckFollowers = "SELECT COUNT(*) as count FROM favtopic WHERE tpid = ?";
            ResultSet followerRs = ctodb.executeQuery(sqlCheckFollowers, topicId);

            int followerCount = 0;
            if (followerRs != null && followerRs.next()) {
                followerCount = followerRs.getInt("count");
                followerRs.close();
            }

            // Delete followers first (cascade delete)
            if (followerCount > 0) {
                String sqlDeleteFollowers = "DELETE FROM favtopic WHERE tpid = ?";
                ctodb.executeUpdate(sqlDeleteFollowers, topicId);
            }

            // Delete the topic
            String sqlDeleteTopic = "DELETE FROM topics WHERE tpid = ?";
            int result = ctodb.executeUpdate(sqlDeleteTopic, topicId);

            if (result > 0) {
                response.sendRedirect("admin5/addtopic.jsp?success=topic_deleted");
            } else {
                response.sendRedirect("admin5/addtopic.jsp?error=delete_failed");
            }

        } catch (Exception e) {
            System.err.println("Error deleting topic: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("admin5/addtopic.jsp?error=server_error");
        }
    }

    /**
     * Check if user is an admin.
     */
    private boolean isAdmin(String username) {
        try {
            String sql = "SELECT role FROM user_profile WHERE username = ?";
            ResultSet rs = ctodb.executeQuery(sql, username);

            if (rs != null && rs.next()) {
                String role = rs.getString("role");
                rs.close();
                return "admin".equalsIgnoreCase(role);
            }
            if (rs != null) rs.close();
            return false;
        } catch (Exception e) {
            System.err.println("Error checking admin status: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getServletInfo() {
        return "Topic Deletion Servlet (Admin)";
    }
}
