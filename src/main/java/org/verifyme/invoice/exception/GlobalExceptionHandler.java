package org.verifyme.invoice.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


@Provider
public class GlobalExceptionHandler
        implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof InvoiceValidationException) {

            return buildResponse(
                    Response.Status.BAD_REQUEST,
                    exception.getMessage()
            );
        }

        if (exception instanceof ExchangeRateNotFoundException) {

            return buildResponse(
                    Response.Status.NOT_FOUND,
                    exception.getMessage()
            );
        }

        if (exception instanceof CurrencyConversionException) {

            return buildResponse(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    exception.getMessage()
            );
        }

        return buildResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "Unexpected server error"
        );
    }

    private Response buildResponse(
            Response.Status status,
            String message
    ) {

        return Response
                .status(status)
                .entity("Error: " + message)
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}