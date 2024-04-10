package org.example;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class App 
{
    public static PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    public static Counter requests = prometheusMeterRegistry.counter("requests");
    public static Timer timer = Timer
            .builder("test.timer")
            .publishPercentiles(0.3,0.5,0.95)
            .publishPercentileHistogram()
            .register(prometheusMeterRegistry);

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
        initEndPoint();
        Runner runner = new Runner();
        runner.run();
    }
}
