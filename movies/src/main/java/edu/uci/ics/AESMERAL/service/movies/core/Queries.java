package edu.uci.ics.AESMERAL.service.movies.core;

import edu.uci.ics.AESMERAL.service.movies.MoviesService;
import edu.uci.ics.AESMERAL.service.movies.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.movies.models.Param;


import javax.ws.rs.QueryParam;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class Queries {
    public static ResultSet getMoviesByInfo(String title, int year, String director, String genre, Boolean hidden, int limit, int offset, String orderby, String direction)
    {
        ResultSet rs = null;

        String query = "SELECT DISTINCT \n" +
                "       M.movie_id,\n" +
                "       title, \n" +
                "       year, \n" +
                "       P.name, \n" +
                "       rating, \n" +
                "       backdrop_path, \n" +
                "       poster_path, \n" +
                "       hidden\n" +
                "       FROM movie AS M\n" +
                "       INNER JOIN person AS P ON M.director_id = P.person_id\n" +
                "       INNER JOIN genre_in_movie AS GIM ON M.movie_id = GIM.movie_id\n" +
                "       INNER JOIN genre AS G ON G.genre_id = GIM.genre_id\n" +
                "       WHERE 1=1\n";
        ArrayList<Param> paramsArrayList = new ArrayList<Param>();
        int counter = 1;
        if(title != null) {
            query = query + "       AND M.title LIKE ? \n";
            paramsArrayList.add(Param.create(Types.VARCHAR, "%" + title + "%" , counter));
            counter++;
        }
        if(year != 0) {
            query = query + "       AND M.year = ? \n";
            paramsArrayList.add(Param.create(Types.INTEGER, year, counter));
            counter++;
        }
        if(director != null){
            query = query + "       AND P.name LIKE ? \n";
            paramsArrayList.add(Param.create(Types.VARCHAR, "%" + director + "%", counter));
            counter++;
        }
        if(genre != null){
            query = query + "       AND G.name = ?\n";
            paramsArrayList.add(Param.create(Types.VARCHAR, genre, counter));
            counter++;
        }
        // do hidden here
        if(hidden == false) {
            query = query + "       AND M.hidden = ?\n";
            paramsArrayList.add(Param.create(Types.BOOLEAN, hidden, counter));
            counter++;
        }
        if(orderby.equals("title")) {
            query = query + String.format("         ORDER BY M.%s %s, M.rating desc\n", orderby, direction);
        }
        else if(orderby.equals("rating")){
            query = query + String.format("         ORDER BY M.%s %s, M.title asc\n", orderby, direction);
        }
        else {
            query = query + String.format("         ORDER BY M.%s %s, M.rating desc\n", orderby, direction);
        }
        // do limit here
        query = query + "       LIMIT ? OFFSET ?\n";
        paramsArrayList.add(Param.create(Types.INTEGER, limit, counter));
        counter++;
        paramsArrayList.add(Param.create(Types.INTEGER, offset, counter));
        counter++;
        Param [] param = new Param[paramsArrayList.size()];
        param = paramsArrayList.toArray(param);
        try{
            rs = MyUtil.prepareStatement(query,param,paramsArrayList.size()).executeQuery();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("SQL error " + e);
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet getMoviesByTags(ArrayList<String> Tags,Boolean hidden, int limit, int offset, String orderby, String direction)
    {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("Building our query ... ");
        String query = "        SELECT DISTINCT\n" +
                "               M.movie_id,\n" +
                "               title,\n" +
                "               year,\n" +
                "               P.name\n," +
                "               rating,\n" +
                "               backdrop_path,\n" +
                "               poster_path,\n" +
                "               hidden\n" +
                "               FROM movie AS M\n" +
                "               INNER JOIN person AS P ON M.director_id = P.person_id\n";
                // dynamically create the inner joins.
                for(int i = 0; i < Tags.size(); i++)
                {
                    String dynamicQuery = String.format("       INNER JOIN keyword_in_movie AS KIM%d ON KIM%d.movie_id = M.movie_id\n",i,i);
                    dynamicQuery = dynamicQuery + String.format("       INNER JOIN keyword AS K%d ON K%d.keyword_id = KIM%d.keyword_id\n",i,i,i);
                    query = query + dynamicQuery;
                }
                query = query + "       WHERE 1=1\n";
                int counter = 1;
                ArrayList<Param> arrayListParam = new ArrayList<Param>();
                int index = 0;
                for(String tag : Tags)
                {
                    query = query + String.format("       AND K%d.name = ?\n",index);
                    index++;
                    arrayListParam.add(Param.create(Types.VARCHAR,tag,counter));
                    counter++;
                }
                if(hidden == false) {
                    query = query + "       AND M.hidden = ? \n";
                    arrayListParam.add(Param.create(Types.BOOLEAN, hidden, counter));
                    //if hidden is true then we just show all of it.;
                    counter++;
                }
                if(orderby.equals("title")) {
                    query = query +  String.format("       ORDER BY M.%s %s, M.rating desc\n", orderby, direction);
                }
                else if(orderby.equals("rating")){
                    query = query +  String.format("       ORDER BY M.%s %s, M.title asc\n", orderby, direction);
                }
                else {
                    query = query +  String.format("       ORDER BY M.%s %s, M.rating desc\n", orderby, direction);
                }
                // do limit here
                query = query + "       LIMIT ? OFFSET ?\n";
                arrayListParam.add(Param.create(Types.INTEGER, limit, counter));
                counter++;
                arrayListParam.add(Param.create(Types.INTEGER, offset, counter));
                counter++;
                Param[] params = new Param[arrayListParam.size()];
                params = arrayListParam.toArray(params);
                try {
                    ServiceLogger.LOGGER.info("creating our prepared statements ... ");
                    rs = MyUtil.prepareStatement(query, params, arrayListParam.size()).executeQuery();
                } catch (SQLException e)
                {
                    ServiceLogger.LOGGER.info("SQL Error " + e);
                    e.printStackTrace();
                }
        return rs;
    }
    public static ResultSet findMoviesByPersonID(String person_id,Boolean hidden, Integer limit, Integer offset, String orderby, String direction)
    {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("building query");
        String query = "SELECT DISTINCT\n" +
                       "M.movie_id,\n" +
                       "title,\n" +
                       "year,\n" +
                       "P0.name,\n" +
                       "rating,\n" +
                       "backdrop_path,\n" +
                       "poster_path,\n" +
                       "hidden\n" +
                       "FROM movie AS M\n" +
                       "INNER JOIN person AS P0 ON M.director_id = P0.person_id\n" +
                       "INNER JOIN person_in_movie AS PIM ON PIM.movie_id = M.movie_id\n" +
                       "INNER JOIN person AS P1 ON P1.person_id = PIM.person_id\n" +
                       "WHERE P1.person_id = ?\n";
        if(hidden == false)
        {
            query = query + "AND hidden = ?";
        }
        if(orderby.equals("title")) {
            query = query +  String.format("       ORDER BY M.%s %s, M.rating desc\n", orderby, direction);
        }
        else if(orderby.equals("rating")){
            query = query +  String.format("       ORDER BY M.%s %s, M.title asc\n", orderby, direction);
        }
        else {
            query = query +  String.format("       ORDER BY M.%s %s, M.rating desc\n", orderby, direction);
        }
        // do limit here
        query = query + "       LIMIT ? OFFSET ?\n";
        try{
            ServiceLogger.LOGGER.info("Query: \n" + query);
            ServiceLogger.LOGGER.info("Creating our prepared statement .. ");
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1,person_id);
            if(!hidden)
            {
                ps.setBoolean(2,hidden);
                ps.setInt(3, limit);
                ps.setInt(4, offset);
            }
            else
            {
                ps.setInt(2,limit);
                ps.setInt(3,offset);
            }
            rs = ps.executeQuery();

        } catch(SQLException e)
        {
            ServiceLogger.LOGGER.info("SQL Error " + e);
            e.printStackTrace();
        }
        return rs;
    }

    public static ResultSet getMovieByID(String ID, Boolean hidden)
    {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("building our query");
        String query =  "SELECT DISTINCT M.*, P.name\n" +
                        "FROM movie AS M\n" +
                        "INNER JOIN person AS P on P.person_id = M.director_id\n" +
                        "WHERE movie_id = ?\n";
        if(hidden == false)
        {
            query = query + "AND hidden = ?\n";
        }
        try{
            ServiceLogger.LOGGER.info("Creating our prepared statement ... ");
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, ID);
            if(!hidden) ps.setBoolean(2, hidden);
            ServiceLogger.LOGGER.info("Executing our query");
            rs = ps.executeQuery();
        } catch (SQLException e)
        {
            ServiceLogger.LOGGER.info("SQL Error " + e);
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet getGenreByMovieID(String ID)
    {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("Building our query");
        String query =  "SELECT DISTINCT G.name, G.genre_id\n" +
                        "FROM genre AS G\n" +
                        "INNER JOIN genre_in_movie AS GIM ON GIM.genre_id = G.genre_id\n" +
                        "INNER JOIN movie AS M on M.movie_id = GIM.movie_id\n" +
                        "WHERE M.movie_id = ?";
        try{
            ServiceLogger.LOGGER.info("Creating our prepared statement ... ");
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1,ID);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("SQL Error " + e);
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet getPeopleByMovieID(String ID)
    {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("Building our query");
        String query =  "SELECT DISTINCT P.person_id, P.name\n" +
                        "FROM person AS P\n" +
                        "INNER JOIN person_in_movie AS PIM ON P.person_id = PIM.person_id\n" +
                        "INNER JOIN movie as M ON M.movie_id = PIM.movie_id\n" +
                        "WHERE M.movie_id = ?";
        try{
            ServiceLogger.LOGGER.info("Creating our prepared statement ...");
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1,ID);
            rs = ps.executeQuery();
        } catch(SQLException e){
            ServiceLogger.LOGGER.info("SQL Error " + e);
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet findPerson(String name)
    {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("building query");
        String query = "SELECT DISTINCT person_id\n" +
                       "FROM person\n" +
                       "WHERE name LIKE ?\n";
        try{
            ServiceLogger.LOGGER.info("Creating our prepared statement ... ");
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1,"%" + name + "%");
            rs = ps.executeQuery();

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("SQL Error " + e);
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet getByPersonID(Integer person_id)
    {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("Building query .. ");
        String query =  "SELECT DISTINCT\n" +
                        "P.person_id,\n" +
                        "P.name,\n" +
                        "G.gender_name,\n" +
                        "P.birthday,\n" +
                        "P.deathday,\n" +
                        "P.biography,\n" +
                        "P.birthplace,\n" +
                        "P.popularity,\n" +
                        "P.profile_path\n" +
                        "FROM person AS P\n" +
                        "INNER JOIN gender AS G ON P.gender_id = G.gender_id\n" +
                        "WHERE P.person_id = ?";
        try{
            ServiceLogger.LOGGER.info("Preparing query statement:\n" + query);
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setInt(1,person_id);
            rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Excuted query statement:\n" + query);
        }catch (SQLException e)
        {
            ServiceLogger.LOGGER.info("Something went wrong with your query: " + e);
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet findPersonInfo(String name, String birthday, String movie_title, Integer limit, Integer offset, String orderby, String direction)
    {
        ResultSet rs = null;
        ServiceLogger.LOGGER.info("Building query ...");
        String query =  "SELECT DISTINCT\n" +
                        "P.person_id,\n" +
                        "P.name,\n" +
                        "P.birthday,\n" +
                        "P.popularity,\n" +
                        "P.profile_path\n" +
                        "FROM person AS P\n" +
                        "INNER JOIN person_in_movie AS PIM on P.person_id = PIM.person_id\n" +
                        "INNER JOIN movie AS M ON PIM.movie_id = M.movie_id\n" +
                        "WHERE 1=1\n";

        ArrayList<Param> paramArrayList = new ArrayList<Param>();
        int counter = 1;
        if(name != null)
        {
            query = query + "AND P.name LIKE ?\n";
            paramArrayList.add(Param.create(Types.VARCHAR, "%" + name + "%",counter));
            counter++;
        }
        if(birthday != null)
        {
            query = query + "AND P.birthday LIKE ?\n";
            paramArrayList.add(Param.create(Types.VARCHAR, "%" + birthday + "%",counter));
            counter++;
        }
        if(movie_title != null)
        {
            query = query + "AND M.title LIKE ?\n";
            paramArrayList.add(Param.create(Types.VARCHAR,"%" + movie_title + "%",counter));
            counter++;
        }
        if(orderby.equals("name")) {
            query = query +  String.format("       ORDER BY P.%s %s, P.popularity desc\n", orderby, direction);
        }
        else if(orderby.equals("birthday")){
            query = query +  String.format("       ORDER BY P.%s %s, P.popularity desc\n", orderby, direction);
        }
        else {
            query = query +  String.format("       ORDER BY P.%s %s, P.name asc\n", orderby, direction);
        }
        // do limit here
        query = query + "       LIMIT ? OFFSET ?\n";
        paramArrayList.add(Param.create(Types.INTEGER, limit, counter));
        counter++;
        paramArrayList.add(Param.create(Types.INTEGER, offset, counter));
        counter++;
        Param[] params = new Param[paramArrayList.size()];
        params = paramArrayList.toArray(params);
        try
        {
            ServiceLogger.LOGGER.info("Creating the prepared statement ... ");
            rs = MyUtil.prepareStatement(query,params,paramArrayList.size()).executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return rs;
    }
}
