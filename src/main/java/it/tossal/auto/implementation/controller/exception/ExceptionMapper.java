package it.tossal.auto.implementation.controller.exception;

import java.util.Map;

/**
 * @author <a href="https://www.linkedin.com/in/federico-tosello/">Tosello Federico</a>
 * @version 1.0.0
 * @since JAVA 17
 */

/**
 * Implement this class a spring bean for support exception handling.
 */
public interface ExceptionMapper {

    class Response {

        private final int statusCode;

        private final String messageResponse;

        public Response(int statusCode, String messageResponse) {
            this.statusCode = statusCode;
            this.messageResponse = messageResponse;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessageResponse() {
            return messageResponse;
        }

    }

    /**
     * @return Map containing the response to return to the client in case of exceptions.
     */
    Map<Class<? extends Exception>, Response> exceptionMapper();

}
