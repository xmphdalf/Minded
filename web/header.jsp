<%--
    Document   : header
    Created on : 12 Jan, 2020, 12:26:22 PM
    Author     : Minded Team
    Version    : 2.0
    Description: Site header with navigation and user profile
--%>
<%@page import="connectdb.ctodb" %>
<%@page import="java.sql.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    // Get website data using prepared statement
    String websiteLogo = "logo.png"; // Default
    try {
        String sqlWebsite = "SELECT logo FROM website_data LIMIT 1";
        ResultSet websiteRs = ctodb.executeQuery(sqlWebsite);
        if (websiteRs != null && websiteRs.next()) {
            websiteLogo = websiteRs.getString("logo");
            websiteRs.close();
        }
    } catch (Exception e) {
        // Use default logo on error
    }

    // Get user profile using prepared statement
    String userImage = "default-avatar.png"; // Default
    String username = (String) session.getAttribute("username");
    if (username != null && !username.trim().isEmpty()) {
        try {
            String sqlProfile = "SELECT image FROM user_profile WHERE username = ?";
            ResultSet profileRs = ctodb.executeQuery(sqlProfile, username);
            if (profileRs != null && profileRs.next()) {
                String img = profileRs.getString("image");
                if (img != null && !img.isEmpty()) {
                    userImage = img;
                }
                profileRs.close();
            }
        } catch (Exception e) {
            // Use default avatar on error
        }
    }
%>

<div class="responsive-header">
    <div class="mh-head first Sticky">
        <span class="mh-btns-left">
            <a class="" href="#menu"><i class="fa fa-align-justify"></i></a>
        </span>
        <span class="mh-text">
            <a href="index.jsp" title=""><img src="images/<%= websiteLogo %>" style="width: 25%" height="50" width="500" alt="Minded Logo"></a>
        </span>
    </div>

    <nav id="menu" class="res-menu">
        <ul>
            <li><a href="index.jsp" title=""><i class="fa fa-newspaper-o fa-1x"></i> Home</a></li>
            <li><a href="topics.jsp" title=""><i class="fa fa-rss-square fa-1x"></i> Topics</a></li>
            <li><a href="notification.jsp" title=""><i class="fa fa-bell-o fa-1x"></i> Notification</a></li>
        </ul>
    </nav>
</div><!-- responsive header -->

<div class="topbar stick">
    <div class="logo">
        <a title="" href="index.jsp"><img src="images/<%= websiteLogo %>" style="width: 55%" height="50" width="500" alt="Minded Logo"></a>
    </div>

    <div class="top-area">
        <ul class="main-menu">
            <li><a href="index.jsp" title=""><i style="position: absolute; margin-top: 17px; margin-left: -35px;" class="fa fa-newspaper-o fa-2x"></i> Home</a></li>
            <li><a href="topics.jsp" title=""><i style="position: absolute; margin-top: 17px; margin-left: -30px;" class="fa fa-rss-square fa-2x"></i> Topics</a></li>
            <li><a href="notification.jsp" title=""><i style="position: absolute; margin-top: 17px; margin-left: -30px;" class="fa fa-bell-o fa-2x"></i> Notification</a></li>
            <li><a href="#" data-toggle="modal" data-target="#myModal" title=""><i style="position: absolute; margin-top: 17px; margin-left: -30px;" class="fa fa-plus-circle fa-2x"></i> Add New Question</a></li>
        </ul>

        <div class="user-img">
            <img src="images/<%= userImage %>" style="margin-top: -5px;" height="45" width="45" alt="User Profile">
            <span class="status f-online"></span>
            <div class="user-setting">
                <a onclick="window.location='profile.jsp';"><i class="ti-user"></i>profile</a>
                <a onclick="window.location='logout.jsp';" title=""><i class="ti-power-off"></i>logout</a>
            </div>
        </div>
    </div>
</div><!-- topbar -->
