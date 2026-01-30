/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectdb;

import java.sql.*;


/**
 *
 * @author ksc
 */
public class ctodb {
    Connection con;
    Statement st;
    ResultSet rs;
    String url="jdbc:mysql://localhost:3306/minded";
    String usr="root";
    String pass="";
    String drv="com.mysql.jdbc.Driver";
    public void conset(){
    try{
       Class.forName(drv);
    con=DriverManager.getConnection(url,usr,pass);  
    st=con.createStatement();
    }
    catch(Exception e){
        e.printStackTrace();
    }
}
    public int exquery(String qry){
       int ans=0;
        try{
            ans=st.executeUpdate(qry); 
            
       }
        catch(Exception e2){
            e2.printStackTrace();
        }
        return ans;
    }
    public ResultSet rsquery(String qry){
        try{
            rs=st.executeQuery(qry); 
            
       }
        catch(Exception e3){
            e3.printStackTrace();
        }
        return rs;
    }
    
    
}
