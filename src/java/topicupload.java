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
 * Servlet for creating new topics (Admin only).
 * Includes admin authentication and prepared statements.
 *
 * @author Minded Team
 * @version 2.0
 */
@WebServlet(urlPatterns = {"/topicupload"})
public class topicupload extends HttpServlet {

    private static final int MAX_TOPIC_NAME_LENGTH = 100;
    private static final int MIN_TOPIC_NAME_LENGTH = 2;

    /**
     * Handles the HTTP POST method for creating topics.
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

        String topicName = request.getParameter("topic");

        // Validate topic name
        if (topicName == null || topicName.trim().isEmpty()) {
            response.sendRedirect("admin5/addtopic.jsp?error=topic_required");
            return;
        }

        // Clean topic name - remove extra whitespace
        topicName = topicName.trim().replaceAll("\\s+", " ");

        // Validate length
        if (topicName.length() < MIN_TOPIC_NAME_LENGTH) {
            response.sendRedirect("admin5/addtopic.jsp?error=topic_too_short");
            return;
        }

        if (topicName.length() > MAX_TOPIC_NAME_LENGTH) {
            response.sendRedirect("admin5/addtopic.jsp?error=topic_too_long");
            return;
        }

        // Sanitize topic name (alphanumeric, spaces, basic punctuation only)
        if (!isValidTopicName(topicName)) {
            response.sendRedirect("admin5/addtopic.jsp?error=invalid_characters");
            return;
        }

        try {
            // Check if topic already exists
            String sqlCheck = "SELECT tpid FROM topics WHERE LOWER(tpname) = LOWER(?)";
            ResultSet rs = ctodb.executeQuery(sqlCheck, topicName);

            if (rs != null && rs.next()) {
                rs.close();
                response.sendRedirect("admin5/addtopic.jsp?error=topic_exists");
                return;
            }
            if (rs != null) rs.close();

            // Insert topic using prepared statement
            String sqlInsert = "INSERT INTO topics(tpname) VALUES(?)";
            int result = ctodb.executeUpdate(sqlInsert, topicName);

            if (result > 0) {
                response.sendRedirect("admin5/addtopic.jsp?success=topic_created");
            } else {
                response.sendRedirect("admin5/addtopic.jsp?error=create_failed");
            }

        } catch (Exception e) {
            System.err.println("Error creating topic: " + e.getMessage());
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

    /**
     * Validate topic name contains only allowed characters.
     */
    private boolean isValidTopicName(String name) {
        // Allow alphanumeric, spaces, hyphens, underscores, and basic punctuation
        return name.matches("^[a-zA-Z0-9\\s\\-_.,!?()]+$");
    }

    @Override
    public String getServletInfo() {
        return "Topic Creation Servlet (Admin)";
    }
}
