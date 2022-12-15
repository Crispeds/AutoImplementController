package it.tossal.auto.implementation.controller.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * @author <a href="https://www.linkedin.com/in/federico-tosello/">Tosello Federico</a>
 * @version 1.0.0
 * @since JAVA 17
 */
public class ExceptionHandler {

    private ExceptionHandler() {}

    /**
     * Handle an exception using the map produced in exceptionToHandle (If @HandleException annotation is used), if the method is not override the response will be 500 - Internal server error
     * @param e Exception to handle
     * @return Response for the client
     */
    public static void handle(ExceptionMapper exceptionMapper,Exception e){
        ExceptionMapper.Response response = Optional.of(exceptionMapper.exceptionMapper().get(e.getClass())).orElse(new ExceptionMapper.Response(500, "Internal server error"));
        throw new ResponseStatusException(HttpStatusCode.valueOf(response.getStatusCode()),response.getMessageResponse());
    }

}
