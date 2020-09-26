package edu.uci.ics.AESMERAL.service.billing.utilities.queries;

import edu.uci.ics.AESMERAL.service.billing.BillingService;
import edu.uci.ics.AESMERAL.service.billing.logger.ServiceLogger;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class modify {

    public static ResultSet checkExist(String email, String movie_id) {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("Building your query");
        String query =  "SELECT DISTINCT *\n" +
                        "FROM cart\n" +
                        "WHERE email = ? AND movie_id = ?";
        try {
            ServiceLogger.LOGGER.info("Query has been built:\n " + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, movie_id);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Something went wrong in your query... ");
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet checkExist(String email)
    {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("Building your query");
        String query =  "SELECT DISTINCT *\n" +
                        "FROM cart\n" +
                        "WHERE email = ?";
        try {
            ServiceLogger.LOGGER.info("Query has been built:\n " + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Something went wrong in your query... ");
            e.printStackTrace();
        }
        return rs;
    }

    public static void insert(String email, String movie_id, Integer quantity)
    {
        ServiceLogger.LOGGER.info("Building your query");
        String query = "INSERT INTO cart (email, movie_id, quantity) VALUES (? , ? , ?)";
        try {
            ServiceLogger.LOGGER.info("Query has been built:\n " + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2,movie_id);
            ps.setInt(3, quantity);
            ps.execute();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong in your query... ");
            e.printStackTrace();
        }
    }
    public static void update(String email, String movie_id, Integer quantity)
    {
        ServiceLogger.LOGGER.info("Building your query");
        String query =  "UPDATE cart SET quantity = ?\n" +
                        "WHERE email = ? AND movie_id = ?";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n " + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setInt(1,quantity);
            ps.setString(2,email);
            ps.setString(3,movie_id);
            ps.execute();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong in your query... ");
            e.printStackTrace();
        }
    }
    public static void delete(String email, String movie_id)
    {
        ServiceLogger.LOGGER.info("Building your query");
        String query = "DELETE FROM cart WHERE email = ? AND movie_id = ?";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n " + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,email);
            ps.setString(2,movie_id);
            ps.execute();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong in your query...");
            e.printStackTrace();
        }
    }
    public static void clear(String email)
    {
        ServiceLogger.LOGGER.info("Building your query");
        String query = "DELETE FROM cart WHERE email = ?";
        try{
            ServiceLogger.LOGGER.info("Query has bee built:\n " + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,email);
            ps.execute();
        } catch(SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong in your query...");
            e.printStackTrace();
        }
    }
    public static ResultSet moviePrice(String movie_id, String email)
    {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("Building your query");
        String query =  "SELECT MP.unit_price, MP.discount, C.quantity\n" +
                        "FROM cart AS C\n" +
                        "INNER JOIN movie_price AS MP ON MP.movie_id = C.movie_id\n" +
                        "WHERE C.movie_id = ? AND C.email = ?";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n" + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,movie_id);
            ps.setString(2, email);
            rs = ps.executeQuery();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong in your query...");
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet insertSale(String email, String movie_id, Integer quantity, Date sale_date)
    {
        ResultSet rs = null;
        String query = "INSERT INTO sale (email,movie_id, quantity, sale_date) VALUES (?, ? , ? , ?)";
        String returnQuery= "SELECT max(sale_id) AS sale_id FROM sale";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n" + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2,movie_id);
            ps.setInt(3, quantity);
            ps.setDate(4,sale_date);
            ps.execute();
            ps = BillingService.getCon().prepareStatement(returnQuery);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Something went wrong in your query...");
            e.printStackTrace();
        }
        return rs;
    }
    public static void InsertTransactionBeforeCapture(Integer sale_id, String token)
    {
        String query = "INSERT INTO transaction (sale_id,token) VALUES (? , ?)";
        try
        {
            ServiceLogger.LOGGER.info("Query has been built:\n" + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setInt(1,sale_id);
            ps.setString(2,token);
            ps.execute();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong with your query.");
            e.printStackTrace();
        }
    }
    public static ResultSet InsertTransactionAfterCapture(String Token, String capture_id)
    {
        ResultSet rs = null;
        String query = "UPDATE transaction SET capture_id = ? WHERE token = ?";

        String recieveQuery = "SELECT email from sale\n" +
                              "WHERE sale_id = (SELECT sale_id FROM transaction WHERE token = ? LIMIT 1)";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n" + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, capture_id);
            ps.setString(2, Token);
            ps.execute();
            ps = BillingService.getCon().prepareStatement(recieveQuery);
            ServiceLogger.LOGGER.info("did we make it here?");
            ps.setString(1, Token);
            ServiceLogger.LOGGER.info("did we make it there?");
            rs = ps.executeQuery();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong your query.");
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet getToken(String email)
    {
        ResultSet rs = null;
        String query =  "SELECT DISTINCT token, capture_id\n" +
                        "FROM transaction\n" +
                        "INNER JOIN sale s on transaction.sale_id = s.sale_id\n" +
                        "WHERE s.email = ?";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n" + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,email);
            rs = ps.executeQuery();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong with your query.");
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet getSale(String token)
    {
        ResultSet rs = null;
        String query =  "SELECT email, sale.movie_id, quantity, unit_price, discount, sale_date\n" +
                        "FROM sale\n" +
                        "INNER JOIN movie_price ON movie_price.movie_id = sale.movie_id\n" +
                        "INNER JOIN transaction ON sale.sale_id = transaction.sale_id\n" +
                        "WHERE transaction.token = ?";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n" + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,token);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.info("Something went wrong with your query.");
        }
        return rs;
    }

}
