package org.example.sample.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.sample.model.ConnectionList;
import org.springframework.amqp.rabbit.connection.ConnectionFactoryContextWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class MessageController {

    ConnectionFactoryContextWrapper wrapper;
    RabbitTemplate template;
    ConnectionList connectionList;

    @GetMapping("/send")
    public void sendDirectToQueue(@RequestParam String message) {
        Stream.of(connectionList.first(), connectionList.second())
                .forEach(connection ->
                        wrapper.run(connection.vhost(), () -> template.convertAndSend(connection.queue(), message)));
    }

}
