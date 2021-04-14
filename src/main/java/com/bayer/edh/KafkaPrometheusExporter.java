package com.bayer.edh;

import kafka.metrics.KafkaMetricsReporter;
import kafka.utils.VerifiableProperties;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by zxian on 7/12/17.
 */
public class KafkaPrometheusExporter implements KafkaMetricsReporter, KafkaPrometheusExporterMBean {
    private static final Logger logger = LoggerFactory.getLogger(KafkaPrometheusExporter.class);
    private boolean initialized = false;
    private boolean running = false;
    private Server server;

    @Override
    public synchronized void init(VerifiableProperties props) {
        if (!initialized) {
            int port = props.getInt("external.kafka.prometheus.port", 18093);
            String reporterConfig = props.getString("external.kafka.prometheus.config", "/etc/kafka/kafka_prometheus.yml");

            try {
                new MBeanCollector(new File(reporterConfig)).register();
                logger.info("Starting web server on port {}", port);
                server = new Server(port);
                ServletHandler handler = new ServletHandler();
                server.setHandler(handler);
                handler.addServletWithMapping(MetricsServlet.class, "/metrics");
                initialized = true;
                startReporter(5);
            } catch (Exception ex) {
                logger.error("Failed to start MBeanCollector.", ex);
            }
        }
    }

    @Override
    public synchronized void startReporter(long pollingPeriodInSeconds) {
        if (initialized && !running) {
            try {
                server.start();
                running = true;
                logger.info("Web server started");
            } catch (Exception ex) {
                logger.error("Failed to start prometheus reporter.", ex);
            }
        }
    }

    @Override
    public synchronized void stopReporter() {
        if (initialized && running) {
            try {
                server.stop();
                running = false;
                logger.info("Stopped prometheus reporter");
            } catch (Exception ex) {
                logger.error("Failed to stop prometheus reporter.", ex);
            }
        }
    }

    @Override
    public String getMBeanName() {
        return "kafka:type=" + KafkaPrometheusExporter.class.getName();
    }
}
