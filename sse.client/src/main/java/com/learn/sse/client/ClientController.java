package com.learn.sse.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalTime;

@RestController
@RequestMapping("/consumer")
public class ClientController {

    private static Logger logger = LoggerFactory.getLogger(ClientController.class);
    private WebClient client = WebClient.create("http://localhost:8080/server");

    @GetMapping("/launch-client")
    public String launchSSEFromSSEWebClient() {
        consumeSSE();
        return "Client launched";
    }

    @Async
    public void consumeSSE() {
        ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<ServerSentEvent<String>>() {
        };

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri("/streamData")
                .retrieve()
                .bodyToFlux(type);

        eventStream.subscribe(content -> logger.info("Current time: {} - Received SSE: name[{}], id [{}], content[{}] ", LocalTime.now(), content.event(), content.id(), content.data()), error -> logger.error("Error receiving SSE: {}", error),
                () -> logger.info("Completed!!!"));
    }
}
