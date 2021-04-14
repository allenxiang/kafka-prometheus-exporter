package com.bayer.edh;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MetricsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(TextFormat.CONTENT_TYPE_004);
        response.setStatus(HttpServletResponse.SC_OK);
        TextFormat.write004(response.getWriter(), CollectorRegistry.defaultRegistry.metricFamilySamples());
        response.getWriter().close();
    }
}