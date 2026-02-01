
<%@page import="connectdb.ctodb" %>
<%@page import="java.util.ArrayList" %>
<%@page import="upload.fileupload" %>
<%@page import="java.sql.*" %>
<%@include file="usrsessionchk.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
String Yourimage = "";
String Yourname = "";
String sessionvar = "";
ctodb c = new ctodb();
try {
    c.conset();
    sessionvar = session.getAttribute("username").toString();
} catch (Exception e) {
    // Log error without exposing details to user
    System.err.println("Index page initialization error: " + e.getMessage());
    response.sendRedirect("login.jsp?error=initialization_failed");
    return;
}
%>
<!DOCTYPE html>
<html lang="en">

<head>
    <%@include file="scrpit.jsp" %>
  
</head>
<body>
    <style>
        .shownot{
         display: none;   
        }
    </style>   
<!--<div class="se-pre-con"></div>-->
<div class="theme-layout">
    <%@include file="header.jsp" %>
	<!-- topbar -->
		
        <section>
		<div class="gap gray-bg">
			<div class="container-fluid">
				<div class="row">
					<div class="col-lg-12">
						<div class="row" id="page-contents">
							<div class="col-lg-3 hideme">
								<aside class="sidebar static hideme">
									<div class="widget">
										<h4 class="widget-title">Followed Topics</h4>
										<ul class="naves">
                                                                                    <%
                                                                                    String upids = "";
                                                                                    String qryforuseridget = "SELECT upid, name FROM user_profile WHERE username = ?";
                                                                                    ResultSet getuseridresultset = ctodb.executeQuery(qryforuseridget, sessionvar);
                                                                                    if (getuseridresultset != null) {
                                                                                        while (getuseridresultset.next()) {
                                                                                            upids = getuseridresultset.getString("upid");
                                                                                            Yourname = getuseridresultset.getString("name");
                                                                                        }
                                                                                        getuseridresultset.close();
                                                                                    }

                                                                            String sqlforicon = "SELECT topics.tpname FROM topics JOIN favtopic ON favtopic.tpid=topics.tpid JOIN user_profile ON user_profile.upid=favtopic.upid WHERE user_profile.username = ?";

                                                                            ResultSet sqlforiconrs = ctodb.executeQuery(sqlforicon, sessionvar);
                                                                            if (sqlforiconrs != null) {
                                                                                while (sqlforiconrs.next()) {
                                                                                    String topicName = sqlforiconrs.getString("topics.tpname");
                                                                                    if (topicName != null && !topicName.isEmpty()) {
                                                                                        // HTML entity encoding for XSS prevention
                                                                                        String safeTopicName = topicName.replace("<", "&lt;")
                                                                                                                         .replace(">", "&gt;")
                                                                                                                         .replace("\"", "&quot;")
                                                                                                                         .replace("'", "&#x27;");
                                                                            %>
											<li>
												<i class="ti-clipboard"></i>
												<a href="customtopic.jsp?tpnm=<%=java.net.URLEncoder.encode(topicName, "UTF-8")%>" title=""><%= safeTopicName %></a>
											</li>
											 <%
                                                                                    }
                                                                                }
                                                                                sqlforiconrs.close();
                                                                            }
                                                                            %>
										</ul>
									</div><!-- Shortcuts -->
									<!-- who's following -->
								</aside>
							</div><!-- sidebar -->
							<div class="col-lg-6">
								<div class="central-meta">
                                                                   <%
                                                                    try {
                                                                        String sqlforprofile2 = "SELECT image, name FROM user_profile WHERE username = ?";
                                                                        ResultSet dataforprofile2 = ctodb.executeQuery(sqlforprofile2, sessionvar);
                                                                        if (dataforprofile2 != null && dataforprofile2.next()) {
                                                                            String userImage = dataforprofile2.getString("image");
                                                                            Yourimage = (userImage != null && !userImage.isEmpty()) ? userImage : "default-avatar.png";
                                                                            // Sanitize filename to prevent path traversal
                                                                            Yourimage = Yourimage.replaceAll("[^a-zA-Z0-9._-]", "_");
                                                                    %>
									<div class="new-postbox">

										<figure>
                                                                                    <img src="images/<%=Yourimage%>" height="50" width="50" alt="User Profile">
										</figure>
										<div class="newpst-input">
											<button style="text-align: start; margin-top: 5px;" data-toggle="modal" data-target="#myModal" class="form-control"><i  class="fa fa-plus-circle fa-1x"></i> Add New Question</button>
										</div>
									</div>
                                                                    <%
                                                                            dataforprofile2.close();
                                                                        }
                                                                    } catch (Exception e) {
                                                                        System.err.println("Error loading user profile: " + e.getMessage());
                                                                    }
                                                                    %>
                                                                </div><!-- add post new box -->
								<div class="loadMore">
                                                                   <%
                                                           ArrayList content=new ArrayList();
                                                    ArrayList pid=new ArrayList();
                                                    ArrayList upvote=new ArrayList();
                                                    ArrayList downvote=new ArrayList();
                                                    ArrayList image=new ArrayList();
                                                    ArrayList date=new ArrayList();
                                                     ArrayList upid=new ArrayList();
                                                    ArrayList propic=new ArrayList();
                                                   ArrayList topicpo=new ArrayList();
                                                    ArrayList proname=new ArrayList();
                                                    
                                                    String sqlforfetchpost="select * from user_profile INNER JOIN user_post on user_profile.upid=user_post.upid ORDER BY pid DESC";
                                                    ResultSet dataforpost=c.rsquery(sqlforfetchpost);
                                                    while(dataforpost.next()){
                                                       propic.add(dataforpost.getString("user_profile.image"));
                                                       topicpo.add(dataforpost.getString("user_post.topic"));
                                                        proname.add(dataforpost.getString("user_profile.name"));
                                                       upvote.add(dataforpost.getString("user_post.upvote"));
                                                        downvote.add(dataforpost.getString("user_post.downvote"));
                                                       
                                                        pid.add(dataforpost.getString("user_post.pid"));
                                                        upid.add(dataforpost.getString("user_profile.upid"));
                                                        content.add(dataforpost.getString("content"));
                                                        image.add(dataforpost.getString("user_post.image"));
                                                        date.add(dataforpost.getString("date"));
                                                        
                                                    }
                                                    String[] propicdt=(String[]) propic.toArray(new String[propic.size()]);
                                                    String[] upiddt=(String[]) upid.toArray(new String[upid.size()]);
                                                    String[] upvotedt=(String[]) upvote.toArray(new String[upvote.size()]);
                                                    String[] downvotedt=(String[]) downvote.toArray(new String[downvote.size()]);
                                                    String[] topicpodt=(String[]) topicpo.toArray(new String[topicpo.size()]);
                                                    String[] piddt=(String[]) pid.toArray(new String[pid.size()]);
                                                    String[] pronamedt=(String[]) proname.toArray(new String[proname.size()]);
                                                    String[] contentdt=(String[]) content.toArray(new String[content.size()]);
                                                    String[] imagedt=(String[]) image.toArray(new String[image.size()]);
                                                    String[] datedt=(String[]) date.toArray(new String[date.size()]);
                                                     
                                                    for(int m=0;m<piddt.length;m++)
                                                        {
                                                        %>
								<div class="central-meta item">
									<div class="user-post">
										<div class="friend-info">
											<figure>
												<%
                                                                                                String safeProPic = propicdt[m] != null ? propicdt[m].replaceAll("[^a-zA-Z0-9._-]", "_") : "default-avatar.png";
                                                                                                %>
												<img src="images/<%=safeProPic%>" alt="User Avatar">
											</figure>
											<div class="friend-name">
												<%
                                                                                                String safeName = pronamedt[m] != null ? pronamedt[m].replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;") : "Anonymous";
                                                                                                String safeDate = datedt[m] != null ? datedt[m].replace("<", "&lt;").replace(">", "&gt;") : "";
                                                                                                %>
												<ins><a href="#" title=""><%=safeName%></a></ins>
												<span>published: <%=safeDate%>  </span>
											</div>
                                                                                         <div class="description">
													<%
                                                                                                        String safeContent = contentdt[m] != null ? contentdt[m].replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;") : "";
                                                                                                        %>
													<h4>
														<%=safeContent %>
													</h4>
												</div>
											<div class="post-meta">
                                                                                             <%
                                                                                            if (imagedt[m] != null && !imagedt[m].isEmpty()) {
                                                                                                String safePostImage = imagedt[m].replaceAll("[^a-zA-Z0-9._-]", "_");
                                                                                            %>
												<img src="images/<%=safePostImage%>" style="height: 100%;width: 100%;" alt="Post Image">
                                                                                            <%}%>
                                                                                           
                                                                                                <div class="we-video-info">
													<ul>
														<li>
															<span class="views" data-toggle="tooltip" title="upvote">
                                                                                                                            <a href="updownvote.jsp?idup=<%=piddt[m]%>"><i class="fa fa-arrow-up" aria-hidden="true"></i></a>
																<ins><%=upvotedt[m]%></ins>
															</span>
														</li>
                                                                                                                <li>
															<span class="views" data-toggle="tooltip" title="downvote">
                                                                                                                            <a href="updownvote.jsp?iddw=<%=piddt[m]%>"><i class="fa fa-arrow-down" aria-hidden="true"></i></a>
																<ins><%=downvotedt[m]%></ins>
															</span>
														</li>
                                                                                                                <li>
                                                                                                                    <span class="comment"  title="Comments">
                                                                                                                        <i  class="fa fa-comments-o"></i>
																
															</span>
                                                                                                                </li>
                                                                                                                <li>
                                                                                                                    <span class="tag"  title="tag">
                                                                                                                      <%
                                                                                                                      String safeTopic = topicpodt[m] != null ? topicpodt[m].replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;") : "";
                                                                                                                      %>
                                                                                                                      <i class="fa fa-tag" aria-hidden="true"></i>  <%=safeTopic%>
                                                                                                                      </span>
														</li>
														
														
													</ul>
												</div>
												
											</div>
                                                                                        
										</div>
                                                                            
										<div class="coment-area">
                                                                                    <%
                                                                               String sqlforcomment = "SELECT post_ans.*, user_post.upid AS post_upid, user_profile.upid, user_profile.image, user_profile.name, post_ans.date FROM post_ans INNER JOIN user_post ON user_post.pid=post_ans.pid INNER JOIN user_profile ON user_profile.upid=post_ans.upid WHERE post_ans.pid = ? ORDER BY post_ans.paid";

                                                                               ResultSet sqlforcommentrs = ctodb.executeQuery(sqlforcomment, piddt[m]);

                                                                               if (sqlforcommentrs != null) {
                                                                                   while (sqlforcommentrs.next()) {

                                                                               %>
                                                                               <ul class="we-comet">
                                                                                           
												<li style="margin-bottom: 0px;">
                                                                                                     <%
                                                                                   String commentUserImage = sqlforcommentrs.getString("image");
                                                                                   String commentUserName = sqlforcommentrs.getString("name");
                                                                                   String commentDate = sqlforcommentrs.getString("date");
                                                                                   String commentContent = sqlforcommentrs.getString("content");

                                                                                   // Sanitize for XSS
                                                                                   String safeCommentImage = (commentUserImage != null) ? commentUserImage.replaceAll("[^a-zA-Z0-9._-]", "_") : "default-avatar.png";
                                                                                   String safeCommentName = (commentUserName != null) ? commentUserName.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;") : "Anonymous";
                                                                                   String safeCommentDate = (commentDate != null) ? commentDate.replace("<", "&lt;").replace(">", "&gt;") : "";
                                                                                   String safeCommentContent = (commentContent != null) ? commentContent.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;") : "";

                                                                                   if(sqlforcommentrs.getString("post_upid").equals(sqlforcommentrs.getString("upid"))){
                                                                                                    %>
													<div class="comet-avatar">
														<img src="images/<%=safeCommentImage%>" style="height: 50px; width: 50px;" alt="User Avatar">
													</div>
													<div class="we-comment">
														<div class="coment-head">
															<h5><a href="#" title=""><%=safeCommentName%></a></h5>
															<span><%=safeCommentDate%></span>

														</div>
														<p><%=safeCommentContent%></p>
													</div>
                                                                                                         <%}else{%>
													<ul style="margin-bottom: 0px;">
														<li style="margin-bottom: 0px;">
															<div class="comet-avatar">
																<img src="images/<%=safeCommentImage%>" style="height: 50px; width: 50px;" alt="User Avatar">
															</div>

															<div class="we-comment">
																<div class="coment-head">
																	<h5><a href="#" title=""><%=safeCommentName%></a></h5>
																	<span><%=safeCommentDate%></span>

																</div>
																<p><%=safeCommentContent%></p>
															</div>

														</li>

													</ul>
                                                                                                         <%}%>
												</li>
												 <%}
                                                                                                 sqlforcommentrs.close();
                                                                                                 }
                                                                                                 %>      
                                                                                        </ul>
                                                                                        
                                                                                        <ul>   
                                                                                                     <li class="post-comment" style="list-style: none; margin-bottom:  -50px; margin-right: 80px;">
                                                                                                         <%
                                                                                    String sqlforprofilepic = "SELECT image FROM user_profile WHERE username = ?";
                                                                                    ResultSet dataforprofilepic = ctodb.executeQuery(sqlforprofilepic, sessionvar);
                                                                                    if (dataforprofilepic != null && dataforprofilepic.next()) {
                                                                                        String commentFormImage = dataforprofilepic.getString("image");
                                                                                        String safeCommentFormImage = (commentFormImage != null && !commentFormImage.isEmpty()) ? commentFormImage.replaceAll("[^a-zA-Z0-9._-]", "_") : "default-avatar.png";
                                                        %>
													<div class="comet-avatar">
														<img src="images/<%=safeCommentFormImage%>" height="50" width="50" alt="Your Avatar">
													</div>
													<div class="post-comt-box">
														<form id="myForm" method="post" action="postans">
                                                                                                                <input class="form-control" name="comment" type="text" placeholder="Post your comment"  required/>
                                                                                                                <input class="form-control" name="user" type="hidden" value="<% out.print(sessionvar);%>"   />
                                                                                                                <input class="form-control" name="pid" type="hidden" value="<%=piddt[m]%>"   />
                                                                                                                
															<div class="add-smiles">
                                                                                                                            <span id="sendcomment"> <i  class="fa fa-send"></i></span>
															</div>
															
                                                                                                                   
														</form>
													</div>
                                                                                                        <%
                                                                                                        dataforprofilepic.close();
                                                                                                        }
                                                                                                        %> 
												</li>
                                                                                                </ul>
											
                                                                                       
										</div>
                                                                              
									</div>
								</div>
							<%}%>	
								</div>
                                                                
							</div><!-- centerl meta -->
							 <div class="col-lg-3">
								<aside class="sidebar static">
									<div class="widget stick-widget is_stuck">
										<h4 class="widget-title">Your Profile</h4>	
										<div class="your-page">
											<figure>
                                                                                            <%
                                                                                            String displayImage = (Yourimage != null && !Yourimage.isEmpty()) ? Yourimage : "default-avatar.png";
                                                                                            String safeName = (Yourname != null) ? Yourname.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;") : "";
                                                                                            %>
                                                                                            <a href="#" title=""><img src="images/<%=displayImage%>" alt="Your Profile"></a>
											</figure>
											<div class="page-meta">
                                                                                            <a href="profile.jsp" title="" class="underline"><%=safeName%></a>
									         	</div>
											<div class="page-likes">
												<ul class="nav nav-tabs likes-btn">
													<li class="nav-item"><a class="active" href="#link1" data-toggle="tab">Answered</a></li>
													 <li class="nav-item"><a class="" href="#link2" data-toggle="tab">Question</a></li>
												</ul>
												<!-- Tab panes -->
                                                                                                 <%
                                                                                                         int Answer = 0;
                                                                                                         String sqlforfetchans = "SELECT COUNT(*) AS answer_count FROM user_profile INNER JOIN post_ans ON user_profile.upid=post_ans.upid WHERE user_profile.username = ?";
                                                                                                         ResultSet dataforans = ctodb.executeQuery(sqlforfetchans, sessionvar);
                                                                                                         if (dataforans != null && dataforans.next()) {
                                                                                                             Answer = dataforans.getInt("answer_count");
                                                                                                             dataforans.close();
                                                                                                         }
                                                     %>
												<div class="tab-content">
												  <div class="tab-pane active fade show " id="link1" >
													<span><%= Answer %></span>
													  <a href="#" title="weekly-likes">Total Answered <%= Answer %> </a>
													  
												  </div>
												  <div class="tab-pane fade" id="link2" >
                                                                                                      <%
                                                                                                         int Question = 0;
                                                                                                         String sqlforfetchposts = "SELECT COUNT(*) AS question_count FROM user_profile INNER JOIN user_post ON user_profile.upid=user_post.upid WHERE user_profile.username = ?";
                                                                                                         ResultSet dataforposts = ctodb.executeQuery(sqlforfetchposts, sessionvar);
                                                                                                         if (dataforposts != null && dataforposts.next()) {
                                                                                                             Question = dataforposts.getInt("question_count");
                                                                                                             dataforposts.close();
                                                                                                         }
                                                     %>
                                                                                                          <span><%= Question %></span>
													  <a href="#" title="weekly-likes">Total Questions asked <%= Question %></a>
													 
												  </div>
												</div>
											</div>
										</div>
									</div>
						</div>	
					</div>
				</div>
			</div>
		</div>	
	</section>
                                                                <script type="text/javascript">
                                                                    
                                                                   function submitOnEnter(e) {
    var theEvent = e || window.event;
    if(theEvent.keyCode == 13) {
        this.submit;
    }
    return true;
}
document.getElementById("myForm").onkeypress = function(e) { return submitOnEnter(e); }



    
                                                                </script>
                                                                <%@include file="addquestionmodal.jsp"  %>
        <%@include file="footer.jsp"  %>
		<!-- footer -->
	
</div>
       <%@include file="sidepanel.jsp" %>
	<!-- side panel -->		
	
        <%@include file="scriptfooter.jsp" %>

</body>	

</html>
