package com.redhat.fuse.sample.route.internal;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.MediaType;

@Component("HealthInternalRoute")
public class HealthInternalRoute extends RouteBuilder {

    static final Logger logger = LoggerFactory.getLogger(HealthInternalRoute.class);

    @Value("${management.server.port}")
    private String port;

    @Override
    public void configure() throws Exception {

        // /--------------------------------------------------\
        // | Internal route definition                        |
        // | description: routes actuator health endpoint     |
        // \--------------------------------------------------/
        from("direct:internal-health")
                .id("direct-internal-health")
                .to("log:list?showHeaders=true&level=DEBUG")
                .removeHeader("origin")
                .removeHeader(Exchange.HTTP_PATH)
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
                .removeHeader("breadcrumbId")
                .to("http4://" + "localhost" + ":" + port + "/" + "actuator/health" + "?connectTimeout=500&bridgeEndpoint=true&copyHeaders=true&connectionClose=true&type=metrics")
                .end();
    }

}
