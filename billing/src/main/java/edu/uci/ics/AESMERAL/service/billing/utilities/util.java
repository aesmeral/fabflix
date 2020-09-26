package edu.uci.ics.AESMERAL.service.billing.utilities;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.AESMERAL.service.billing.logger.ServiceLogger;
import edu.uci.ics.AESMERAL.service.billing.models.ResponseModels.ResponseModel;

import java.io.IOException;

public class util {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T, S extends ResponseModel> T modelMapper(
            String jsonString, Class<T> className, S responseModel)
    {
        ServiceLogger.LOGGER.info("Mapping object from String: " + jsonString);
        try {
            return MAPPER.readValue(jsonString, className);

        } catch (IOException e) {
            ServiceLogger.LOGGER.info("something went wrong");
            setException(e, responseModel);
        }

        ServiceLogger.LOGGER.info("Mapping Object Failed: " + responseModel.getResult());
        return null;
    }

    private static <S extends ResponseModel> void setException(IOException e, S responseModel)
    {
        if (e instanceof JsonMappingException) {
            responseModel.setResult(Result.JSON_MAPPING_EXCEPTION);

        } else if (e instanceof JsonParseException) {
            responseModel.setResult(Result.JSON_PARSE_EXCEPTION);

        } else {
            responseModel.setResult(Result.INTERNAL_SERVER_ERROR);
        }
    }

    public static <T> T modelMapper(String jsonString, Class<T> className)
    {
        ObjectMapper mapper = new ObjectMapper();

        ServiceLogger.LOGGER.info("Mapping object");

        try {
            return mapper.readValue(jsonString, className);

        } catch (IOException e) {
            ServiceLogger.LOGGER.info("Mapping Object Failed: " + e.getMessage());
            return null;
        }
    }
}
