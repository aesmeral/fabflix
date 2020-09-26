package edu.uci.ics.AESMERAL.service.movies.core;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.AESMERAL.service.movies.MoviesService;
import edu.uci.ics.AESMERAL.service.movies.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.movies.models.BaseResponseModel;
import edu.uci.ics.AESMERAL.service.movies.models.Param;
import edu.uci.ics.AESMERAL.service.movies.resources.Search;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class MyUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T, S extends BaseResponseModel> T modelMapper(
            String jsonString, Class<T> className, S responseModel)
    {
        ServiceLogger.LOGGER.info("Mapping object from String");

        try {
            return MAPPER.readValue(jsonString, className);

        } catch (IOException e) {
            setException(e, responseModel);
        }

        ServiceLogger.LOGGER.info("Mapping Object Failed: " + responseModel.getMessage());

        return null;
    }

    private static <S extends BaseResponseModel> void setException(IOException e, S responseModel)
    {
        if (e instanceof JsonMappingException) {
            responseModel.setResult(ResultResponse.JSON_MAPPING_EXCEPTION);

        } else if (e instanceof JsonParseException) {
            responseModel.setResult(ResultResponse.JSON_PARSE_EXCEPTION);

        } else {
            responseModel.setResult(ResultResponse.INTERNAL_SERVER_ERROR);

        }
    }

    public static <T> T modelMapper(String jsonString, Class<T> className)
    {
        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("Mapping Object");
        try{
            return mapper.readValue(jsonString, className);
        }catch (IOException e)
        {
            ServiceLogger.LOGGER.info("Mapping Object Failed:" + e.getMessage());
            return null;
        }
    }
    public static PreparedStatement prepareStatement(String query, Param [] paramList, int paramListSize) throws SQLException {
        ServiceLogger.LOGGER.info("Preparing Statement");
        PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
        ServiceLogger.LOGGER.info("Entering parameters");
        for(int i = 0; i < paramListSize; i++)
            ps.setObject(paramList[i].getLocation(), paramList[i].getParam(), paramList[i].getType());
        ServiceLogger.LOGGER.info("Query ready\n" + ps.toString());
        return ps;
    }

}
