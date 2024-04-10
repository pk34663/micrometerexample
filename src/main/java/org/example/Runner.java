package org.example;

import io.micrometer.core.instrument.Timer;

import java.util.Random;

import static org.example.App.prometheusMeterRegistry;
import static org.example.App.requests;

public class Runner {
    private int counter = 0;
    public Runner() {}

    public void randomDelay() throws InterruptedException
    {
        Timer.Sample sample = Timer.start(prometheusMeterRegistry);
        Random random = new Random();
        Thread.sleep(random.nextInt(1001));
        sample.stop(prometheusMeterRegistry.timer("test.timer"));
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
