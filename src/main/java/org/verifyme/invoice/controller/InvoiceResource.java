package org.verifyme.invoice.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.verifyme.invoice.dto.InvoiceRequest;
import org.verifyme.invoice.service.InvoiceCalculationService;

@Path("/invoice")
public class InvoiceResource {

    @Inject
    InvoiceCalculationService calculationService;

    @POST
    @Path("/total")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String calculateTotal(
            InvoiceRequest request
    ) {

        return calculationService
                .calculateInvoiceTotal(request);
    }
}
