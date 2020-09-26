package edu.uci.ics.AESMERAL.service.billing.utilities.queries;

import edu.uci.ics.AESMERAL.service.billing.BillingService;
import edu.uci.ics.AESMERAL.service.billing.logger.ServiceLogger;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class discountQueries {

    public static int createDiscount(String code, Float discount, Date date_start, Date date_end, Integer limit){
        ServiceLogger.LOGGER.info("Building your query ... ");
        String query = "INSERT into discount_code (code, discount, sale_start, sale_end, usage_limit) VALUES (?, ?, ?, ? , ?)";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n" + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,code);
            ps.setFloat(2,discount);
            ps.setDate(3,date_start);
            ps.setDate(4,date_end);
            ps.setInt(5, limit);
            ps.execute();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong in your query ...");
            e.printStackTrace();
            return -1;
        }
        return 1;
    }
    public static ResultSet applyDiscount(String email, String code){
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("Building your query ... ");
        String query =  "SELECT AC.email, AC.discount_id, AC.times_used, DC.discount, DC.usage_limit, DC.sale_end\n" +
                        "FROM applied_code AS AC\n" +
                        "INNER JOIN discount_code AS DC ON DC.discount_id = AC.discount_id\n" +
                        "WHERE DC.code = ?\n" +
                        "AND AC.email = ?";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n" + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,code);
            ps.setString(2,email);
            rs = ps.executeQuery();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong in your query");
            e.printStackTrace();
        }
        return rs;
    }
    public static void applyNewDiscount(String email, String code){
        String query = "INSERT INTO applied_code (email, discount_id, times_used) VALUES (?, (SELECT discount_id FROM discount_code WHERE code = ?), 0)";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n" + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,email);
            ps.setString(2,code);
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void updateUserDiscount(String email, Integer discount_id, Integer times_used){
        String query =  "UPDATE applied_code SET times_used = ?\n" +
                        "WHERE email = ? AND discount_id = ?";
        try{
            ServiceLogger.LOGGER.info("Query has been built:\n" + query);
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setInt(1,times_used);
            ps.setString(2,email);
            ps.setInt(3, discount_id);
            ps.execute();
        } catch (SQLException e){
            ServiceLogger.LOGGER.info("Something went wrong in your query ...");
            e.printStackTrace();
        }
    }
}
