import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import mail.JavaMailUtil;
import connectdb.ctodb;
import encrydecry.endestr;
import java.sql.ResultSet;
import otp.generateotp;

/**
 * Password reset servlet with OTP verification.
 * Handles secure password reset flow with email OTP verification.
 *
 * @author Minded Team
 * @version 2.0
 */
@WebServlet(urlPatterns = {"/resetpassword"})
public class resetpassword extends HttpServlet {

    private static final long OTP_EXPIRY_MS = 15 * 60 * 1000; // 15 minutes

    /**
     * Handles the HTTP <code>POST</code> method for password reset.
     * Supports three actions: requestOTP, verifyOTP, resetPassword
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            // Legacy support - direct password reset (for backward compatibility)
            handleLegacyReset(request, response);
            return;
        }

        switch (action) {
            case "requestOTP":
                handleOTPRequest(request, response);
                break;
            case "verifyOTP":
                handleOTPVerification(request, response);
                break;
            case "resetPassword":
                handlePasswordReset(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    /**
     * Handle OTP request - generate and send OTP to user's email.
     */
    private void handleOTPRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String email = request.getParameter("email");
        String name = request.getParameter("name");

        if (email == null || email.isEmpty()) {
            response.getWriter().write("{\"success\":false,\"message\":\"Email is required\"}");
            return;
        }

        try {
            // Verify user exists (using prepared statement)
            String sql = "SELECT upid, username, email, name FROM user_profile WHERE email = ?";
            if (name != null && !name.isEmpty()) {
                sql += " AND name = ?";
            }

            ResultSet rs = (name != null && !name.isEmpty())
                    ? ctodb.executeQuery(sql, email, name)
                    : ctodb.executeQuery(sql, email);

            if (rs != null && rs.next()) {
                // User exists - generate and send OTP
                String otp = generateotp.generateOTP();
                HttpSession session = request.getSession();

                // Store OTP details in session
                session.setAttribute("resetEmail", email);
                session.setAttribute("resetOTP", otp);
                session.setAttribute("otpGeneratedTime", System.currentTimeMillis());
                session.setAttribute("otpUsed", false);

                // Send OTP via email
                try {
                    JavaMailUtil.sendOTPEmail(email, otp);
                    response.getWriter().write("{\"success\":true,\"message\":\"OTP sent to your email\"}");
                } catch (Exception e) {
                    System.err.println("Failed to send OTP email: " + e.getMessage());
                    response.getWriter().write("{\"success\":false,\"message\":\"Failed to send email. Please try again.\"}");
                }

                rs.close();
            } else {
                // Don't expose whether email exists (security best practice)
                response.getWriter().write("{\"success\":true,\"message\":\"If this email exists, OTP has been sent\"}");
            }

        } catch (Exception e) {
            System.err.println("OTP request error: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"An error occurred. Please try again.\"}");
        }
    }

    /**
     * Handle OTP verification - validate entered OTP.
     */
    private void handleOTPVerification(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String enteredOTP = request.getParameter("otp");
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.getWriter().write("{\"success\":false,\"message\":\"Session expired. Please request new OTP.\"}");
            return;
        }

        String sessionOTP = (String) session.getAttribute("resetOTP");
        Long otpGeneratedTime = (Long) session.getAttribute("otpGeneratedTime");
        Boolean otpUsed = (Boolean) session.getAttribute("otpUsed");

        // Validate OTP
        if (sessionOTP == null || otpGeneratedTime == null) {
            response.getWriter().write("{\"success\":false,\"message\":\"No OTP found. Please request new OTP.\"}");
            return;
        }

        if (otpUsed != null && otpUsed) {
            response.getWriter().write("{\"success\":false,\"message\":\"OTP already used. Please request new OTP.\"}");
            return;
        }

        // Check expiration
        long currentTime = System.currentTimeMillis();
        if (currentTime - otpGeneratedTime > OTP_EXPIRY_MS) {
            response.getWriter().write("{\"success\":false,\"message\":\"OTP expired. Please request new OTP.\"}");
            return;
        }

        // Verify OTP matches
        if (sessionOTP.equals(enteredOTP)) {
            session.setAttribute("otpVerified", true);
            response.getWriter().write("{\"success\":true,\"message\":\"OTP verified successfully\"}");
        } else {
            response.getWriter().write("{\"success\":false,\"message\":\"Invalid OTP. Please try again.\"}");
        }
    }

    /**
     * Handle password reset - update password after OTP verification.
     */
    private void handlePasswordReset(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String newPassword = request.getParameter("newpassword");
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("login.jsp?error=session_expired");
            return;
        }

        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        String email = (String) session.getAttribute("resetEmail");

        if (otpVerified == null || !otpVerified) {
            response.sendRedirect("login.jsp?error=otp_not_verified");
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            response.getWriter().write("{\"success\":false,\"message\":\"Password must be at least 6 characters\"}");
            return;
        }

        try {
            // Hash new password
            String hashedPassword = endestr.getMd5(newPassword);

            // Update password using prepared statement (SQL injection prevention)
            String sql = "UPDATE user_profile SET password = ? WHERE email = ?";
            int result = ctodb.executeUpdate(sql, hashedPassword, email);

            if (result > 0) {
                // Mark OTP as used
                session.setAttribute("otpUsed", true);
                session.removeAttribute("resetOTP");
                session.removeAttribute("otpVerified");
                session.removeAttribute("otpGeneratedTime");

                response.sendRedirect("login.jsp?success=password_reset");
            } else {
                response.getWriter().write("{\"success\":false,\"message\":\"Failed to update password\"}");
            }

        } catch (Exception e) {
            System.err.println("Password reset error: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"An error occurred. Please try again.\"}");
        }
    }

    /**
     * Legacy password reset handler for backward compatibility.
     * @deprecated Use new OTP-based flow instead
     */
    @Deprecated
    private void handleLegacyReset(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String newPassword = request.getParameter("newpasswordishere");
            String email = request.getParameter("emailresetpassword");
            String name = request.getParameter("nameresetpass");

            if (newPassword == null || email == null) {
                response.sendRedirect("login.jsp?error=invalid_params");
                return;
            }

            // Hash password
            String hashedPassword = endestr.getMd5(newPassword);

            // Use prepared statement (SQL injection prevention)
            String sql = "UPDATE user_profile SET password = ? WHERE email = ?";
            if (name != null && !name.isEmpty()) {
                sql += " AND name = ?";
                ctodb.executeUpdate(sql, hashedPassword, email, name);
            } else {
                ctodb.executeUpdate(sql, hashedPassword, email);
            }

            response.sendRedirect("login.jsp");

        } catch (Exception ex) {
            System.err.println("Legacy reset error: " + ex.getMessage());
            ex.printStackTrace();
            response.sendRedirect("login.jsp?error=reset_failed");
        }
    }

    @Override
    public String getServletInfo() {
        return "Password Reset Servlet with OTP Verification";
    }
}
