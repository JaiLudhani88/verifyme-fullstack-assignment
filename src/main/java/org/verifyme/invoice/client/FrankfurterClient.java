package org.verifyme.invoice.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.verifyme.invoice.dto.ExchangeRateResponse;

import java.util.List;

@RegisterRestClient(configKey = "frankfurter")
@Path("/v2")
public interface FrankfurterClient {

    @GET
    @Path("/rates")
    List<ExchangeRateResponse> getExchangeRates(
            @QueryParam("date") String date,
            @QueryParam("base") String base,
            @QueryParam("quotes") String quotes
    );
}
