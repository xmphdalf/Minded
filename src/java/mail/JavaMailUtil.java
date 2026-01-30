package mail;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Email utility for sending OTP emails via Gmail SMTP.
 * Uses TLS encryption and environment variables for credentials.
 *
 * @author Minded Team
 * @version 2.0
 */
public class JavaMailUtil {

    // Email configuration from environment variables (for security)
    private static final String SMTP_HOST = System.getenv().getOrDefault("MAIL_HOST", "smtp.gmail.com");
    private static final String SMTP_PORT = System.getenv().getOrDefault("MAIL_PORT", "587");
    private static final String MAIL_USERNAME = System.getenv().getOrDefault("MAIL_USERNAME", "mindedisus@gmail.com");
    private static final String MAIL_PASSWORD = System.getenv().getOrDefault("MAIL_PASSWORD", "mindedmail527");
    private static final String MAIL_FROM = System.getenv().getOrDefault("MAIL_FROM", "noreply@minded.com");

    /**
     * Send OTP email to user's registered email address.
     *
     * @param to Recipient email address
     * @param otp One-time password to send
     * @throws MessagingException if email sending fails
     */
    public static void sendOTPEmail(String to, String otp) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_USERNAME, MAIL_PASSWORD);
            }
        });

        Message message = prepareOTPMessage(session, to, otp);
        Transport.send(message);

        System.out.println("✓ OTP email sent successfully to: " + to);
    }

    /**
     * Prepare OTP email message with professional HTML template.
     *
     * @param session Email session
     * @param to Recipient email
     * @param otp One-time password
     * @return Prepared message
     * @throws MessagingException if message preparation fails
     */
    private static Message prepareOTPMessage(Session session, String to, String otp)
            throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(MAIL_FROM));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Minded - Password Reset OTP");

        String htmlContent = buildOTPEmailTemplate(otp);
        message.setContent(htmlContent, "text/html; charset=utf-8");

        return message;
    }

    /**
     * Build professional HTML email template for OTP.
     *
     * @param otp One-time password
     * @return HTML email content
     */
    private static String buildOTPEmailTemplate(String otp) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; }" +
                ".container { max-width: 600px; margin: 20px auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".header { background: #4f93ce; color: white; padding: 20px; border-radius: 10px 10px 0 0; text-align: center; }" +
                ".content { padding: 30px 20px; text-align: center; }" +
                ".otp-box { background: #f8f9fa; border: 2px dashed #4f93ce; padding: 20px; margin: 20px 0; border-radius: 5px; }" +
                ".otp-code { font-size: 32px; font-weight: bold; color: #4f93ce; letter-spacing: 5px; }" +
                ".footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; padding-top: 20px; border-top: 1px solid #ddd; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>Minded</h1>" +
                "<p>Password Reset Request</p>" +
                "</div>" +
                "<div class='content'>" +
                "<h2>Hello!</h2>" +
                "<p>You requested a password reset for your Minded account.</p>" +
                "<p>Your One-Time Password (OTP) is:</p>" +
                "<div class='otp-box'>" +
                "<div class='otp-code'>" + otp + "</div>" +
                "</div>" +
                "<p><strong>This OTP will expire in 15 minutes.</strong></p>" +
                "<p>If you did not request this password reset, please ignore this email and your password will remain unchanged.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>This is an automated email. Please do not reply.</p>" +
                "<p>&copy; 2026 Minded - Q&A Community Platform</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Legacy method for backward compatibility.
     *
     * @deprecated Use {@link #sendOTPEmail(String, String)} instead
     */
    @Deprecated
    public static void sendmail(String to, String pass) throws Exception {
        sendOTPEmail(to, pass);
    }
}
