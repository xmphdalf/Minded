import connectdb.ctodb;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
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
 * Servlet for creating new questions/posts.
 * Handles text content and optional image uploads.
 *
 * @author Minded Team
 * @version 2.0
 */
@MultipartConfig(
    maxFileSize = 5 * 1024 * 1024,      // 5MB max file size
    maxRequestSize = 10 * 1024 * 1024    // 10MB max request size
)
@WebServlet(urlPatterns = {"/addquestion"})
public class addquestion extends HttpServlet {

    private static final int MAX_CONTENT_LENGTH = 5000;

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

        try {
            // Get user ID using prepared statement
            String sqlGetUpid = "SELECT upid FROM user_profile WHERE username = ?";
            ResultSet rs = ctodb.executeQuery(sqlGetUpid, username);

            if (rs == null || !rs.next()) {
                response.sendRedirect("login.jsp?error=user_not_found");
                return;
            }

            int upid = rs.getInt("upid");
            rs.close();

            // Get form parameters
            String content = request.getParameter("content");
            String topic = request.getParameter("stopic");

            // Validate content
            if (content == null || content.trim().isEmpty()) {
                response.sendRedirect(request.getHeader("referer") + "?error=content_required");
                return;
            }

            if (content.length() > MAX_CONTENT_LENGTH) {
                response.sendRedirect(request.getHeader("referer") + "?error=content_too_long");
                return;
            }

            // Sanitize content (prevent XSS)
            content = sanitize(content);

            // Handle file upload
            String imageName = null;
            Part filePart = request.getPart("fileimg");
            if (filePart != null && filePart.getSize() > 0) {
                fileupload uploader = new fileupload();
                imageName = uploader.uploadme(filePart);
            }

            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
            dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            String formattedDate = dateFormat.format(new Date());

            // Insert post using prepared statement
            int result;
            if (imageName != null && !imageName.isEmpty()) {
                String sql = "INSERT INTO user_post(upid, content, image, date, topic) VALUES(?, ?, ?, ?, ?)";
                result = ctodb.executeUpdate(sql, upid, content, imageName, formattedDate, topic);
            } else {
                String sql = "INSERT INTO user_post(upid, content, date, topic) VALUES(?, ?, ?, ?)";
                result = ctodb.executeUpdate(sql, upid, content, formattedDate, topic);
            }

            if (result > 0) {
                String referer = request.getHeader("referer");
                if (referer != null) {
                    response.sendRedirect(referer);
                } else {
                    response.sendRedirect("index.jsp");
                }
            } else {
                response.sendRedirect(request.getHeader("referer") + "?error=post_failed");
            }

        } catch (Exception e) {
            System.err.println("Error creating post: " + e.getMessage());
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
        return "Question/Post Creation Servlet";
    }
}
