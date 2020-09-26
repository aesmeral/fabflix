package edu.uci.ics.AESMERAL.service.gateway.core;

import edu.uci.ics.AESMERAL.service.gateway.logger.ServiceLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class myQueries {

    public static void insert(Connection con, String transaction_id, String email, String session_id, String response, Integer http_status){
        ServiceLogger.LOGGER.info("Building our query.. ");
        String query =  "INSERT INTO responses (transaction_id, email, session_id, response, http_status) " +
                        "VALUES (?,?,?,?,?)";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, transaction_id);
            ps.setString(2,email);
            ps.setString(3,session_id);
            ps.setString(4,response);
            ps.setInt(5,http_status);
            ServiceLogger.LOGGER.info("Query:\n" + query);
            ps.execute();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Something went wrong in your query ...");
            e.printStackTrace();
        }
    }
    public static ResultSet getResponse(Connection con, String transaction_id){
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("Building our query ... ");
        String query = "SELECT * FROM responses WHERE transaction_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, transaction_id);
            ServiceLogger.LOGGER.info("Query:\n" + query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Something went wrong in your query ...");
            e.printStackTrace();
        }
        return rs;
    }
    public static void removeResponse(Connection con, String transaction_id){
        ServiceLogger.LOGGER.info("Building our query.. ");
        String query = "DELETE FROM responses WHERE transaction_id = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,transaction_id);
            ServiceLogger.LOGGER.info("Query:\n" + query);
            ps.execute();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong in your query ...");
            e.printStackTrace();
        }
    }
}
