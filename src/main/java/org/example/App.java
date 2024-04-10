package org.example;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App 
{
    public static PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    public static void initEndPoint()
    {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/prometheus", httpExchange -> {
                String response = prometheusMeterRegistry.scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });

            new Thread(server::start).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main( String[] args ) throws InterruptedException
    {
        Metrics.globalRegistry.add(prometheusMeterRegistry);
        initEndPoint();
        for (MeterRegistry registry : Metrics.globalRegistry.getRegistries()) {
            System.out.println(registry.toString());
        }

        Runner runner = new Runner();
        runner.run();
    }
}