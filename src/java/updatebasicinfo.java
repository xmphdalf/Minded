import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import connectdb.ctodb;
import java.sql.ResultSet;

/**
 * User profile update servlet.
 * Handles secure profile information updates with authorization checks.
 *
 * @author Minded Team
 * @version 2.0
 */
@WebServlet(urlPatterns = {"/updatebasicinfo"})
public class updatebasicinfo extends HttpServlet {

    /**
     * Handles the HTTP POST method for profile updates.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check if user is logged in
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp?error=not_logged_in");
            return;
        }

        String loggedInUsername = (String) session.getAttribute("username");
        String profileId = request.getParameter("biid");

        // Authorization check - verify user is updating their own profile
        if (profileId == null || !isAuthorized(loggedInUsername, profileId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to update this profile");
            return;
        }

        try {
            // Get form parameters
            String name = request.getParameter("biname");
            String email = request.getParameter("biemail");
            String mobile = request.getParameter("bimobile");
            String day = request.getParameter("biday");
            String month = request.getParameter("bimonth");
            String year = request.getParameter("biyear");
            String hometown = request.getParameter("bihometown");
            String aboutme = request.getParameter("biaboutme");
            String work = request.getParameter("biwork");
            String education = request.getParameter("bieducation");
            String gender = request.getParameter("bigender");

            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                response.sendRedirect(request.getHeader("referer") + "?error=name_required");
                return;
            }

            if (email == null || !isValidEmail(email)) {
                response.sendRedirect(request.getHeader("referer") + "?error=invalid_email");
                return;
            }

            // Sanitize inputs (prevent XSS)
            name = sanitize(name);
            hometown = sanitize(hometown);
            aboutme = sanitize(aboutme);
            work = sanitize(work);
            education = sanitize(education);

            // Validate character limits
            if (aboutme != null && aboutme.length() > 500) {
                response.sendRedirect(request.getHeader("referer") + "?error=aboutme_too_long");
                return;
            }

            // Build SQL query with prepared statement
            String sql;
            Object[] params;

            // Check if birthdate is provided
            if (isValidDate(day, month, year)) {
                String birthdate = year + "-" + month + "-" + day;
                sql = "UPDATE user_profile SET name = ?, gender = ?, email = ?, birthdate = ?, " +
                      "mobile = ?, hometown = ?, aboutme = ?, work = ?, education = ? WHERE upid = ?";
                params = new Object[]{name, gender, email, birthdate, mobile, hometown, aboutme, work, education, profileId};
            } else {
                // Update without birthdate
                sql = "UPDATE user_profile SET name = ?, gender = ?, email = ?, mobile = ?, " +
                      "hometown = ?, aboutme = ?, work = ?, education = ? WHERE upid = ?";
                params = new Object[]{name, gender, email, mobile, hometown, aboutme, work, education, profileId};
            }

            // Execute update using prepared statement (SQL injection prevention)
            int result = ctodb.executeUpdate(sql, params);

            if (result > 0) {
                // Update session if user changed their own name or email
                session.setAttribute("user_name", name);
                session.setAttribute("user_email", email);

                // Redirect back to profile with success message
                String referer = request.getHeader("referer");
                if (referer != null) {
                    response.sendRedirect(referer + "?success=profile_updated");
                } else {
                    response.sendRedirect("profile.jsp?success=profile_updated");
                }
            } else {
                response.sendRedirect(request.getHeader("referer") + "?error=update_failed");
            }

        } catch (Exception e) {
            System.err.println("Profile update error: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getHeader("referer") + "?error=server_error");
        }
    }

    /**
     * Check if user is authorized to update the profile.
     */
    private boolean isAuthorized(String username, String profileId) {
        try {
            String sql = "SELECT upid FROM user_profile WHERE username = ? AND upid = ?";
            ResultSet rs = ctodb.executeQuery(sql, username, profileId);

            boolean authorized = rs != null && rs.next();

            if (rs != null) {
                rs.close();
            }

            return authorized;
        } catch (Exception e) {
            System.err.println("Authorization check error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate email format.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Basic email validation regex
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Validate date components.
     */
    private boolean isValidDate(String day, String month, String year) {
        if (day == null || month == null || year == null) {
            return false;
        }
        if (day.trim().isEmpty() || month.trim().isEmpty() || year.trim().isEmpty()) {
            return false;
        }

        try {
            int d = Integer.parseInt(day);
            int m = Integer.parseInt(month);
            int y = Integer.parseInt(year);

            return d >= 1 && d <= 31 && m >= 1 && m <= 12 && y >= 1900 && y <= 2100;
        } catch (NumberFormatException e) {
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
        // Basic HTML entity encoding
        return input.replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;")
                   .replace("&", "&amp;");
    }

    @Override
    public String getServletInfo() {
        return "User Profile Update Servlet";
    }
}
