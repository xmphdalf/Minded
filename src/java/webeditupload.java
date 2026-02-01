import connectdb.ctodb;
import java.io.IOException;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import upload.fileupload;

/**
 * Servlet for updating website content (Admin only).
 * Handles title image, title text, and logo updates.
 *
 * @author Minded Team
 * @version 2.0
 */
@WebServlet(urlPatterns = {"/webeditupload"})
@MultipartConfig(
    maxFileSize = 10 * 1024 * 1024,      // 10MB max file size
    maxRequestSize = 25 * 1024 * 1024    // 25MB max request size
)
public class webeditupload extends HttpServlet {

    private static final int MAX_TITLE_LENGTH = 200;

    /**
     * Handles the HTTP POST method for updating website content.
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

        String idStr = request.getParameter("myid");
        String title = request.getParameter("title");

        // Validate ID
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect("admin5/website.jsp?error=invalid_id");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("admin5/website.jsp?error=invalid_id");
            return;
        }

        // Validate title
        if (title == null || title.trim().isEmpty()) {
            response.sendRedirect("admin5/website.jsp?error=title_required");
            return;
        }

        if (title.length() > MAX_TITLE_LENGTH) {
            response.sendRedirect("admin5/website.jsp?error=title_too_long");
            return;
        }

        // Sanitize title (prevent XSS)
        title = sanitize(title);

        try {
            // Verify website_data record exists
            String sqlCheck = "SELECT id FROM website_data WHERE id = ?";
            ResultSet checkRs = ctodb.executeQuery(sqlCheck, id);

            if (checkRs == null || !checkRs.next()) {
                if (checkRs != null) checkRs.close();
                response.sendRedirect("admin5/website.jsp?error=record_not_found");
                return;
            }
            checkRs.close();

            // Handle file uploads
            fileupload uploader = new fileupload();

            Part file1Part = request.getPart("file1"); // Title image
            Part file2Part = request.getPart("file2"); // Logo

            String file1Name = null;
            String file2Name = null;

            // Upload title image if provided
            if (file1Part != null && file1Part.getSize() > 0) {
                file1Name = uploader.uploadme(file1Part);
            }

            // Upload logo if provided
            if (file2Part != null && file2Part.getSize() > 0) {
                file2Name = uploader.uploadme(file2Part);
            }

            // If no new files, use existing values
            String existingTitleImg = request.getParameter("mytiim");
            String existingLogo = request.getParameter("mylog");

            if (file1Name == null || file1Name.isEmpty()) {
                file1Name = existingTitleImg;
            }

            if (file2Name == null || file2Name.isEmpty()) {
                file2Name = existingLogo;
            }

            // Update using prepared statement
            String sqlUpdate = "UPDATE website_data SET titleimg = ?, title = ?, logo = ? WHERE id = ?";
            int result = ctodb.executeUpdate(sqlUpdate, file1Name, title, file2Name, id);

            if (result > 0) {
                response.sendRedirect("admin5/website.jsp?success=content_updated");
            } else {
                response.sendRedirect("admin5/website.jsp?error=update_failed");
            }

        } catch (Exception e) {
            System.err.println("Error updating website content: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("admin5/website.jsp?error=server_error");
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
        return "Website Content Update Servlet (Admin)";
    }
}
