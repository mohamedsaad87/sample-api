package com.redhat.fuse.sample.route.rest;

import com.redhat.fuse.sample.model.ApiResponse;
import com.redhat.fuse.sample.model.health.Health;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component("IntegrationRestRoute")
public class IntegrationRestRoute extends RouteBuilder {

    @Value("${api.version}")
    private String apiVersion;

    @Value("${api.title}")
    private String apiTitle;

    @Value("${api.description}")
    private String apiDescription;

    @Value("${api.hostname}")
    private String apiHostname;

    @Override
    public void configure() throws Exception {
        // /--------------------------------------------------\
        // | Configure REST endpoint                          |
        // \--------------------------------------------------/

        // Access-Control-Allow-Origin
        restConfiguration()
            .contextPath("/api/v" + apiVersion)
            .apiContextPath("/api-docs")
                .apiProperty("api.title", apiTitle)
                .apiProperty("api.description", apiDescription)
                .apiProperty("api.version", apiVersion)
                .apiProperty("schemes", "https")
            .host(apiHostname)
            .apiProperty("api.version", apiVersion)
            .dataFormatProperty("prettyPrint", "true")
            .bindingMode(RestBindingMode.json)
            .enableCORS(true)
            //.corsAllowCredentials(true)
            //.corsHeaderProperty("Access-Control-Allow-Origin","*")
            .corsHeaderProperty("Access-Control-Allow-Headers", "Authorization, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Content-Length")
            .clientRequestValidation(true); // see: https://camel.apache.org/manual/latest/rest-dsl.html#_client_request_validation

        // /--------------------------------------------------\
        // | Expose route w/ REST Health Endpoint             |
        // \--------------------------------------------------/

        rest("/status").id("status-endpoint")
            .produces(MediaType.APPLICATION_JSON.getType())
            .consumes(MediaType.APPLICATION_JSON.getType())
            .skipBindingOnErrorCode(false) // enable json marshalling for body in case of errors

            .get().description("Health")
                .outType(Health.class)
                .responseMessage().code(200).responseModel(Health.class).endResponseMessage()
                .responseMessage().code(500).responseModel(ApiResponse.class).endResponseMessage()
                .route().routeId("status")
                    .streamCaching()
                    .to("log:post-list?showHeaders=true&level=DEBUG")
                    .to("direct:internal-health")
                    .unmarshal().json(JsonLibrary.Jackson, Health.class)
                .endRest();

        // /--------------------------------------------------\
        // | Expose route w/ REST Health Endpoint             |
        // \--------------------------------------------------/

        rest("/prometheus").id("prometheus-endpoint")
            .produces(MediaType.TEXT_PLAIN.getType())
            .consumes(MediaType.TEXT_PLAIN.getType())
            .skipBindingOnErrorCode(false) // enable json marshalling for body in case of errors

            .get().description("Prometheus")
            .route().routeId("prometheus")
                .streamCaching()
                .to("log:post-list?showHeaders=true&level=DEBUG")
                .to("direct:internal-prometheus")
            .endRest();

    }

}
