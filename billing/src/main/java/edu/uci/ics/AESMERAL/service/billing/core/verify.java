package edu.uci.ics.AESMERAL.service.billing.core;

import edu.uci.ics.AESMERAL.service.billing.logger.ServiceLogger;

public class verify {

    public static Boolean checkMovie_ID(String movie_id)
    {
        String prefix = movie_id.substring(0,2);
        if(!prefix.equals("tt") && !prefix.equals("cs")) return false;
        String suffix = movie_id.substring(2);
        String regex = "[0-9]+";
        if(!suffix.matches(regex)) return false;
        else return true;
    }
}
