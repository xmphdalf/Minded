<%--
    Document   : updownvote
    Created on : 20 Feb, 2020, 2:13:50 AM
    Author     : Minded Team
    Version    : 2.0
    Description: Legacy voting page - redirects to secure voting servlet
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Session validation - user must be logged in
    if (session == null || session.getAttribute("username") == null) {
        response.sendRedirect("login.jsp?error=not_logged_in");
        return;
    }

    // Check for upvote request
    String upvoteId = request.getParameter("idup");
    if (upvoteId != null && !upvoteId.trim().isEmpty()) {
        // Validate that it's a number
        try {
            Integer.parseInt(upvoteId);
            response.sendRedirect("vote?pid=" + upvoteId + "&type=up");
            return;
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getHeader("referer") + "?error=invalid_post_id");
            return;
        }
    }

    // Check for downvote request
    String downvoteId = request.getParameter("iddw");
    if (downvoteId != null && !downvoteId.trim().isEmpty()) {
        // Validate that it's a number
        try {
            Integer.parseInt(downvoteId);
            response.sendRedirect("vote?pid=" + downvoteId + "&type=down");
            return;
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getHeader("referer") + "?error=invalid_post_id");
            return;
        }
    }

    // No valid vote parameter
    response.sendRedirect(request.getHeader("referer") + "?error=invalid_request");
%>
