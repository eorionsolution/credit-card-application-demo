package com.eorionsolution.demo.bpms.creditcardapplicationdemo.adapter.inbound;

import com.eorionsolution.demo.bpms.creditcardapplicationdemo.domain.CamundaVariable;
import com.eorionsolution.demo.bpms.creditcardapplicationdemo.domain.VariableDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ApiController {
    private int result = 0;

    @GetMapping(path = "/event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> event() {
        return Flux.just(result);
    }

    @PostMapping("/event/{result}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void event(@PathVariable("result") int result) {
        this.result = result;
    }

    @PostMapping(value = "/app/{age}/{limitation}")
    public Mono<String> apply(@PathVariable("age") int age, @PathVariable("limitation") int limitation) {
        log.info("age: {}, limitation: {}", age, limitation);
        var ageV = new VariableDefinition(age);
        var limitationV = new VariableDefinition(limitation);
        var vars = new CamundaVariable();
        vars.getVariables().put("age", ageV);
        vars.getVariables().put("limitation", limitationV);

        var client = WebClient.builder()
                .baseUrl("http://127.0.0.1:8080/engine-rest/process-definition/key/application-demo/start")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        return client.post().body(BodyInserters.fromValue(vars)).retrieve().bodyToMono(String.class);
    }
}
