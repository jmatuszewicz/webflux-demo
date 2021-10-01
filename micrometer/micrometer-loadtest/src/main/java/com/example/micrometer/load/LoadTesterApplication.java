package com.example.micrometer.load;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.web.reactive.function.client.WebClient;

public class LoadTesterApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadTesterApplication.class);

    private static final NormalDistribution NORMAL_DISTRIBUTION = new NormalDistribution(200, 20);

    private static final Duration ONE_SECOND = Duration.ofSeconds(1);

    private final WebClient client;

    private final AtomicLong responseCounter;

    private final AtomicLong requestCounter;

    public LoadTesterApplication() {
        this.client = WebClient.create("http://localhost:8080/cities");
        this.requestCounter = new AtomicLong(0l);
        this.responseCounter = new AtomicLong(0l);
    }

    public void run() {
        Flux.fromStream(normalDistributionStream())
                .delayElements(ONE_SECOND)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::sendRequests)
                .subscribe(this::onResponse);
        Flux.never().blockLast();
    }

    private void onResponse(String body) {
        long counter = responseCounter.incrementAndGet();
        LOGGER.debug("Response received:  {}  total number of responses: {}", body, counter);
    }

    private Flux<String> sendRequests(Integer numOfRequests) {
        LOGGER.info("Sending {} requests", numOfRequests);
        return Flux.fromStream(IntStream.range(0, numOfRequests).boxed())
                .flatMap(this::sendRequest);
    }

    private Mono<String> sendRequest(Integer ind) {
        long counter = requestCounter.incrementAndGet();
        LOGGER.debug("Sending request, total number of requests: {}", counter);
        return client.get()
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class));
    }

    private Stream<Integer> normalDistributionStream() {
        return Stream.iterate(0, i -> (int) NORMAL_DISTRIBUTION.sample());
    }

    public static void main(String[] args) {
        new LoadTesterApplication().run();
    }

}
