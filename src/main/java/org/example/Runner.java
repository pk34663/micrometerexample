package org.example;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;

import java.time.Duration;
import java.util.Random;

import static org.example.App.prometheusMeterRegistry;

public class Runner {
    private int counter = 0;
    private final Counter requests = Counter
                                        .builder("requests")
                                        .description("number of requests")
                                        .register(Metrics.globalRegistry);
    private final static Timer timer = Timer
            .builder("test.timer")
            .publishPercentiles(0.3,0.5,0.95)
            .publishPercentileHistogram()
            .serviceLevelObjectives(Duration.ofMillis(250),Duration.ofMillis(500),Duration.ofMillis(750),Duration.ofMillis(800))
            .minimumExpectedValue(Duration.ofMillis(1))
            .maximumExpectedValue(Duration.ofSeconds(10))
            .register(Metrics.globalRegistry);
    public Runner() {}

    public void randomDelay() throws InterruptedException
    {
        Timer.Sample sample = Timer.start(Metrics.globalRegistry);
        Random random = new Random();
        Thread.sleep(random.nextInt(1001));
        sample.stop(Metrics.globalRegistry.timer("test.timer"));
    }
    public void run() throws InterruptedException
    {
        while(true)
        {
            requests.increment();
            randomDelay();
            counter++;
        }
    }
}