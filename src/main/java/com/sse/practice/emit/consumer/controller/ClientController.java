package com.sse.practice.emit.consumer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalTime;

@Slf4j
@RestController
@RequestMapping("/sse-consumer")
public class ClientController {

    private WebClient client = WebClient.create("http://localhost:8081/sse-server/");

    @GetMapping("/launch-sse-client")
    public String launchSSEFromSSEWebClient() {
        consumeSSE();
        return "Lauched event client from SSE! Check the logs...";
    }

    @GetMapping("/launch-flux-client")
    public String launchFluxFromSSEWebClient() {
        consumeFlux();
        return "Launched event client from Flux! Check the logs...";
    }

    @GetMapping("/launch-sse-from-flux-endpoint-client")
    public String launchFluxFromFluxWebClient() {
        consumeSSEFromFluxEndpoint();
        return "Launch event client from Flux Endpoint! Check the logs...";
    }

    @Async
    public void consumeSSE() {
        ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<ServerSentEvent<String>>() {
        };

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri("/stream-sse")
                .retrieve()
                .bodyToFlux(type);

        eventStream.subscribe(content -> log.info("Current time: {} - Received SSE: name [{}], id [{}], content [{}] ",
                LocalTime.now(),
                content.event(),
                content.id(),
                content.data()),
                error -> log.error("Error receiving SSE:{}", error.toString()),
                () -> log.info("Completed!"));
    }

    @Async
    public void consumeFlux() {
        Flux<String> stringStream = client.get()
                .uri("/stream-flux")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class);

        stringStream.subscribe(
                content -> log.info("Current time: {} - Received content: {} ", LocalTime.now(), content),
                error -> log.error("Error retrieving content: {}", error.toString()),
                () -> log.info("Completed!!!"));
    }

    @Async
    public void consumeSSEFromFluxEndpoint() {
        ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<>() {
        };

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri("/stream-flux")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(type);

        eventStream.subscribe(content -> log.info("Current time: {} - Received SSE: name [{}], id [{}], content[{}] ",
                                LocalTime.now(),
                                content.event(),
                                content.id(),
                                content.data()),
                error -> log.error("Error receiving SSE: {}", error.toString()),
                () -> log.info("Completed!!!"));
    }
}