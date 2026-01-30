<%-- 
    Document   : login
    Created on : 11 Dec, 2019, 3:10:24 PM
    Author     : Mihir
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="connectdb.ctodb"%>
<%@page import="encrydecry.endestr"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <%@include file="scrpit.jsp" %>
    
    <style>
        body{
            overflow: hidden;
        }
        .showmeerror{
            display: none;
        }
        .newpassdiv{
            display: none;
        }
        </style> 
</head>
<body>
<!--<div class="se-pre-con"></div>-->
<div class="theme-layout">
	<div class="container-fluid pdng0">
		<div class="row merged">
			<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
				<div class="land-featurearea">
					<div class="land-meta">
						
						<div >
                                                    <span><img src="images/20191211_1528560.9050489080374585.png" alt=""></span>
						</div>
                                                       
					</div>	
				</div>
			</div>
			<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
				<div class="login-reg-bg">
					<div class="log-reg-area sign">
						<h2 class="log-title">Login</h2>
							
                                                <form method="post" action="login.jsp">
							<div class="form-group">	
                                                            <input type="text" id="input" required="required" name="unm"/>
							  <label class="control-label" for="input">Username</label><i class="mtrl-select"></i>
							</div>
							<div class="form-group">	
							  <input type="password" required="required" name="pass"/>
							  <label class="control-label" for="input">Password</label><i class="mtrl-select"></i>
							</div>
							
							<a href="#" title="" class="forgot-pwd" data-toggle="modal" data-target="#myModal">Forgot Password?</a>
                                                        
                                                        <div class="alert alert-warning alert-dismissible fade show showmeerror"  role="alert">
                                                            <strong>Oops!</strong> You should check your credentials which you filled above.
                                                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                                              <span aria-hidden="true">&times;</span>
                                                            </button>
                                                        </div>
							<div class="submit-btns">
								<input class="mtr-btn signup" style="background-color:#4f93ce; border-color:#4f93ce; color:white;" type="submit" name="log" value="Login" />
								<button class="mtr-btn signup" type="button"><span>Register</span></button>
							</div>
						</form>
					</div>
					<div class="log-reg-area reg">
						<h2 class="log-title">Register</h2>
							
                                                <form method="post" action="login.jsp">
							<div class="form-group">	
                                                            <input type="text" required="required" name="name"/>
							  <label class="control-label" for="input">Name</label><i class="mtrl-select"></i>
							</div>
							<div class="form-group">	
                                                            <input type="text" required="required" id="un" name="username"/>
							  <label class="control-label" for="input">User Name</label><i class="mtrl-select"></i>
							</div>
							<div  >
                                                        <h6 id="msgbox" class="text-danger showmeerror">
                                                                
                                                        </h6>
                                                            <h6 id="msgbox2" class="text-success showmeerror">
                                                                
                                                        </h6>
                                                        </div>
                                                        <div class="form-group">	
                                                            <input type="password" id="pass" required="required" name="password" />
							  <label class="control-label" for="input">Password</label><i class="mtrl-select"></i>
							</div>
                                                    
							<div class="form-radio">
							  <div class="radio">
								<label>
								  <input type="radio" name="radio" checked="checked" value="Male"/><i class="check-box"></i>Male
								</label>
							  </div>
							  <div class="radio">
								<label>
                                                                    <input type="radio" name="radio" value="Female"/><i class="check-box"></i>Female
								</label>
							  </div>
							</div>
							<div class="form-group">	
							  <input type="text" required="required" name="email"/>
							  <label class="control-label" for="input">Email</label><i class="mtrl-select"></i>
                                                          <br>
                                                          <a href="#" title="" class="already-have" style="margin-left: -35px;">Already have an account?</a>
							</div>
							<div class="submit-btns">
                                                            
                                                            <input class="mtr-btn signup" style="background-color:#4f93ce; border-color:#4f93ce; color:white;" id="btnSubmit" type="submit" name="reg" value="Register" />
                                                     </div>
						</form>  
                                        </div>
				</div>
			</div>
		</div>
	</div>
</div>
<form action="resetpassword" method="post">
<div class="modal fade" id="myModal" role="dialog">
    <div class="modal-dialog">
    
      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header" style="background-color: #1fb6ff">
          
            <h4 class="modal-title" style="color: black" >Forget Password</h4>
          <button type="button" class="close" data-dismiss="modal">&times;</button>
        </div>
        <div class="modal-body">
            <h6>Enter your registered Name here:</h6>
            						<div class="form-group">	
                                                            <input type="text" id="resetname" required="required" autocomplete="off" name="nameresetpass"/>
							  <label class="control-label" for="input">Name</label><i class="mtrl-select"></i>
                                                          <br>
                                                          
                                                        </div>
            <h6>Enter your registered email here:</h6>
            						<div class="form-group">	
                                                            <input type="text" id="resetemail" required="required" autocomplete="off" name="emailresetpassword"/>
							  <label class="control-label" for="input">Email</label><i class="mtrl-select"></i>
                                                          <br>
                                                          
                                                        </div>
           <h6>Enter your One time password here:</h6>
            						<div class="form-group">	
                                                            <input type="text" id="resetotp" required="required" autocomplete="off" name="newotpreset"/>
							  <label class="control-label" for="input">OTP</label><i class="mtrl-select"></i>
                                                          <br>
                                                       
                                                        </div>
           <div id="newpassid" class="newpassdiv">
               <h6 >Enter New password here:</h6>
            						<div class="form-group">	
                                                            <input type="text" id="resetnewpass" required="required" autocomplete="off" name="newpasswordishere"/>
							  <label class="control-label" for="input">New password</label><i class="mtrl-select"></i>
                                                          <br>
                                                       
                                                        </div>
           </div>
            
        </div>
        <div class="modal-footer">
            <input type="submit" id="submitresetpass" class="btn btn-primary" name="Submit" value="Submit"/>
          <button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
        </div>
      </div>
    
    </div>
  </div>
</form>
                                                          
	<script data-cfasync="false" src="../../cdn-cgi/scripts/5c5dd728/cloudflare-static/email-decode.min.js"></script><script src="js/main.min.js"></script>
	<script src="js/script.js"></script>
        <script>
            
       function showerror() {
    if( $( ".alert" ).hasClass( "showmeerror" )) {
      $( ".alert" ).removeClass( "showmeerror" );
      
    } else {
      $( ".alert" ).addClass( "showmeerror" );
      
    }
};
    </script>
<script>
    var password="mimo";
        $("#un").blur(function(){
           
        
        
         $.ajax({
                url: 'check.jsp',
                data: 'unm='+ $('#un').val(),
                type: 'post',
                success: function(msg){
                  
                if(msg.slice(6) === 'ERROR') 
                {    
                     $("#btnSubmit").attr("disabled", true);
                    $( "#msgbox" ).removeClass( "showmeerror" );
                     if( $( "#msgbox2" ).hasClass( "showmeerror" )) {   
                    
                }else{
                    $( "#msgbox2" ).addClass("showmeerror" );
                }
                 
                $("#msgbox").html("*username is already taken");
                
                }
                else
                {
                    if(!$("#un").val()){
                          $( "#msgbox2" ).addClass("showmeerror" );
                          $( "#msgbox" ).addClass( "showmeerror" );
                     }else{
                      $("#btnSubmit").attr("disabled", false);
                     $( "#msgbox2" ).removeClass( "showmeerror" );
                   if( $( "#msgbox" ).hasClass( "showmeerror" )) {   
                    
                }else{
                    $( "#msgbox" ).addClass( "showmeerror" );
                }
                
                 $("#msgbox2").html("*username is available");
                     } 
                }
                
 
           }}); 
        });
        $("#resetemail").blur(function(){
        $("#submitresetpass").attr("disabled",true);    
        $.ajax({
                url: 'resetpass.jsp',
                data: 'resetemail='+ $('#resetemail').val()+'&nameresetpass='+$("#resetname").val(),
                type: 'post',
                success: function(msg){
                  
                  password=msg.slice(5);
                  
                  
           }}); 
        });
        $("#resetotp").keyup(function(){
            
                if($('#resetotp').val() === password){
                    $('#newpassid').removeClass("newpassdiv");
                    $("#submitresetpass").attr("disabled",false);
                    
                }else{
                    $('#newpassid').addClass("newpassdiv");
                    $("#submitresetpass").attr("disabled",true);
                }
                
        }); 
        $("#resetnewpass").keyup(function(){
                    if($('#resetotp').val() === password){
                     $("#submitresetpass").attr("disabled",false);
                    
                }else{
                    $('#newpassid').addClass("newpassdiv");
                    $("#submitresetpass").attr("disabled",true);
                }
                   
                
        }); 
        
        
           
    
    </script>
</body>	
 
</html>

<%
    int temp = 0;
    ResultSet getdata = null;

    // Handle registration
    String chk = request.getParameter("reg");
    if (chk != null) {
        try {
            String nm = request.getParameter("name");
            String unm = request.getParameter("username");
            String pass = request.getParameter("password");
            String eml = request.getParameter("email");
            String gnd = request.getParameter("radio");

            // Generate profile picture initial
            String propic = unm.toUpperCase().substring(0, 1);

            // Hash password using static method
            String hashedPass = endestr.getMd5(pass);

            // Use prepared statement to prevent SQL injection
            String sql = "INSERT INTO user_profile(name, username, password, gender, email, image, coverpic) VALUES(?, ?, ?, ?, ?, ?, ?)";
            int res = ctodb.executeUpdate(sql, nm, unm, hashedPass, gnd, eml, propic + ".png", "cover.png");

            if (res > 0) {
                // Registration successful - auto login
                session.setAttribute("username", unm);
                session.setAttribute("user_email", eml);
                response.sendRedirect("index.jsp");
            }
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Handle login
    String chk2 = request.getParameter("log");
    if (chk2 != null) {
        try {
            String username = request.getParameter("unm");
            String password = request.getParameter("pass");

            // Hash password for comparison
            String hashedPass = endestr.getMd5(password);

            // Use prepared statement to prevent SQL injection
            String sql = "SELECT upid, username, password, email, name FROM user_profile WHERE username = ? AND password = ?";
            getdata = ctodb.executeQuery(sql, username, hashedPass);

            if (getdata != null && getdata.next()) {
                // Login successful
                session.setAttribute("username", getdata.getString("username"));
                session.setAttribute("user_email", getdata.getString("email"));
                session.setAttribute("user_id", getdata.getInt("upid"));
                session.setAttribute("user_name", getdata.getString("name"));
                temp = 1;

                getdata.close();
                response.sendRedirect("index.jsp");
            }

            if (temp == 0) {
                out.print("<script>showerror();</script>");
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            out.print("<script>showerror();</script>");
        } finally {
            if (getdata != null) {
                try {
                    getdata.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
%>
    