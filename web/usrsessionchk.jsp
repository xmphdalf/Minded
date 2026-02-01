<%--
    Document   : usrsessionchk
    Created on : Feb 12, 2020, 1:34:15 PM
    Author     : Minded Team
    Version    : 2.0
    Description: Session validation for protected pages
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Session validation - check if user is logged in
    String username = (String) session.getAttribute("username");
    if (username == null || username.trim().isEmpty()) {
        // User not logged in, redirect to login
        response.sendRedirect("login.jsp?error=session_expired");
        return;
    }
%>
