package com.redhat.fuse.sample.route.internal;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component("PrometheusInternalRoute")
public class PrometheusInternalRoute extends RouteBuilder {

    static final Logger logger = LoggerFactory.getLogger(PrometheusInternalRoute.class);

    @Value("${prometheus.port}")
    private String port;

    @Override
    public void configure() throws Exception {

        // /--------------------------------------------------\
        // | Internal route definition                        |
        // | description: routes actuator prometheus endpoint |
        // \--------------------------------------------------/
        from("direct:internal-prometheus")
                .id("direct-internal-prometheus")
                .to("log:list?showHeaders=true&level=DEBUG")
                .removeHeader("origin")
                .removeHeader(Exchange.HTTP_PATH)
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
                .removeHeader("breadcrumbId")
                .to("http4://" + "localhost" + ":" + port + "/" + "actuator/prometheus" + "?connectTimeout=500&bridgeEndpoint=true&copyHeaders=true&connectionClose=true&type=metrics")
                .end();
    }

}
