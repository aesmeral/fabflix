package edu.uci.ics.AESMERAL.service.idm.core;

import edu.uci.ics.AESMERAL.service.idm.IDMService;
import edu.uci.ics.AESMERAL.service.idm.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.idm.security.Session;

import java.security.Provider;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MyQueries {

    public MyQueries() {}

    public ResultSet retrievalWithEmail(String table, String email) {
        ResultSet rs = null;
        try{

            String query = String.format("SELECT * FROM %s WHERE email = ?", table);
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,email);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");
        }
        catch(SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve users records.");
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet retrievalWithSessionID(String SessionID) {
        ResultSet rs = null;
        try{

            String query = String.format("SELECT * FROM session WHERE session_id = ?");
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,SessionID);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");
        }
        catch(SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve session records.");
            e.printStackTrace();
        }
        return rs;
    }

    public void InsertNewUser(String email, String salt, String hashedPassword)
    {
        try{
            String query = "insert into user (email, plevel, status, salt, pword) values (?,?,?,?,?)";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,email);
            ps.setInt(2,5);
            ps.setInt(3,1);
            ps.setString(4,salt);
            ps.setString(5,hashedPassword);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded");
        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query failed: Unable to execute insert into user");
            e.printStackTrace();
        }
    }
    public void InsertNewSession(Session s)
    {
        try {
            String query = "Insert into session (session_id, email, status, time_created, last_used, expr_time) values (?,?,?,?,?,?)";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, String.valueOf(s.getSessionID()));
            ps.setString(2, s.getEmail());
            ps.setInt(3, 1);
            ps.setTimestamp(4, s.getTimeCreated());
            ps.setTimestamp(5, s.getLastUsed());
            ps.setTimestamp(6, s.getExprTime());
            ServiceLogger.LOGGER.info("Trying query" + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded");
        } catch (SQLException e){
            ServiceLogger.LOGGER.warning("Query failed: Unable to execute insert into session");
            e.printStackTrace();
        }
    }
    public void updateSession(String session_id, int status)
    {
        try{
            String query = "Update session SET status = ? WHERE 'session_id' = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setInt(1,status);
            ps.setString(2,session_id);
            ServiceLogger.LOGGER.info("Trying query"  + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded");
        } catch(SQLException e){
            ServiceLogger.LOGGER.warning("Query failed: Unable to execute update into session");
            e.printStackTrace();
        }
    }
    public void InsertUserStatus(int status_id, String status)
    {
        try{
            String query = "Insert into user_status (status_id, status) values (?,?)";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setInt(1,status_id);
            ps.setString(2,status);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded");
        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query failed: Unable to execute insert into user_status");
            e.printStackTrace();
        }
    }
}
