package com.infotech;

import java.sql.*;

import java.util.Properties;

import javax.naming.*;
import javax.sql.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DbConn {
  private static String datasource = null;
  private static Connection cnn = null;

  public DbConn() {
      
  }

  public static Connection getPooledConnection() {
    datasource = Utils.getParameter("datasource");
    if( datasource == null || datasource.length() <= 0 ) return getPooledConnection_JDBC();

    try {
      Context initial = new InitialContext();
      DataSource ds = (DataSource)initial.lookup(datasource);
      Connection dsCnn = ds.getConnection();
      return(dsCnn);
    }
    catch (Exception ex) {
        Utils.showError("Connection Error : " + ex.getMessage());
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        Utils.showError("Connection Error : " + errors.toString());      
        ex.printStackTrace();
    }
    return null;
  } 

  public static Connection getPooledConnection_JDBC() {
    try {
      if( cnn != null && !cnn.isClosed() ) return cnn;

      String driver    = Utils.getParameter("driver");
      String url       = Utils.getParameter("url");
      String userid    = Utils.getParameter("userid");
      String password  = Utils.getParameter("password");

      Properties props = new Properties(); 
      props.setProperty("password", DesEncrypter.decrypt(password)); 
      props.setProperty("user", userid); 
      props.put("v$session.program", "INF_PHARMACYONDUTY"); 

      java.sql.Driver IfmxDrv = (java.sql.Driver) Class.forName(driver).newInstance(); 
      cnn = java.sql.DriverManager.getConnection(url, props);
        
      //java.sql.Driver IfmxDrv = (java.sql.Driver) Class.forName(driver).newInstance();
      //cnn = java.sql.DriverManager.getConnection(url, userid, password);
    }
    catch (Exception ex) {
        Utils.showError("Exception: Connect Error Message : " + ex.getMessage());
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        Utils.showError("Exception: " + errors.toString());      
        ex.printStackTrace();
        cnn = null;
    }

    return cnn;
  } 
  public static void waitTillConnect() {
    Connection test = null;

    while( test == null ) {
      try {
        Thread.currentThread().sleep(10000);
        test = getPooledConnection();
      }
      catch (Exception ex) {
        ;
      }
    } 
    return;
  } 

  public static void closeConnection(Connection dsCnn) {
    
     if( datasource == null || datasource.length() <= 0 ) return;
     try {
       if( dsCnn == cnn ) cnn = null;
       dsCnn.close();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return;
  }
  
   public static long getNextValueFromSequence(String sequenceName, Connection cnn) {
       PreparedStatement pstmt = null;
       ResultSet rset = null;
       String sql = null;
       long nextSeqValue = 0;
       try {
         sql = "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";
         pstmt = cnn.prepareStatement(sql);
         pstmt.clearParameters();
         rset = pstmt.executeQuery();
         if (rset.next()) {
           nextSeqValue = rset.getLong(1);
         }
         rset.close();
         pstmt.close();
       } catch (Exception ex) {
         ex.printStackTrace();
       } finally {
         try { if( rset != null ) rset.close(); } catch (Exception e) {;}
         try { if( pstmt != null ) pstmt.close(); } catch (Exception e) {;}
       }
       return nextSeqValue;
     }
}
