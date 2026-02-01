<%--
    Document   : logout
    Created on : 11 Feb, 2020, 10:19:12 PM
    Author     : Minded Team
    Version    : 2.0
    Description: Secure session logout and redirect
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Invalidate the current session if it exists
    if (session != null) {
        // Clear all session attributes
        session.removeAttribute("username");
        session.removeAttribute("user_name");
        session.removeAttribute("user_email");
        session.removeAttribute("upid");

        // Invalidate the entire session
        session.invalidate();
    }

    // Redirect to login page
    response.sendRedirect("login.jsp?logout=success");
%>
