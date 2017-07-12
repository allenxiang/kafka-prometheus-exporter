package com.monsanto.kafka;

import fi.iki.elonen.NanoHTTPD;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

/**
 * Created by zxian on 7/12/17.
 */
public class KafkaPrometheusExporterServer extends NanoHTTPD {
    private static final Logger logger = LoggerFactory.getLogger(KafkaPrometheusExporter.class);

    public KafkaPrometheusExporterServer(int port) {
        super(port);
    }

    public Response serve(IHTTPSession session) {
        switch (session.getUri()) {
            case "/": return newFixedLengthResponse("Ok");
            case "/metrics": return getMetrics();
            default: return newFixedLengthResponse(Response.Status.NOT_FOUND, "plain/text", "Not found");

        }
    }

    private Response getMetrics() {
        StringWriter sw = new StringWriter();
        try {
            TextFormat.write004(sw, CollectorRegistry.defaultRegistry.metricFamilySamples());
            return newFixedLengthResponse(Response.Status.OK, TextFormat.CONTENT_TYPE_004, sw.toString());
        } catch (Exception ex) {
            logger.error("Failed to write metrics.", ex);
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "plain/text", ex.getMessage());
        }
    }
}
