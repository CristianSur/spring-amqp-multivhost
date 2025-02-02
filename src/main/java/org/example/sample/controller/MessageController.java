package org.example.sample.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.sample.model.ConnectionList;
import org.example.sample.model.TestDto;
import org.springframework.amqp.rabbit.connection.ConnectionFactoryContextWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.CREATED;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class MessageController {

    ConnectionFactoryContextWrapper wrapper;
    RabbitTemplate template;
    ConnectionList connectionList;

    @PostMapping("/send")
    @ResponseStatus(CREATED)
    public void sendDirectToQueue(@RequestBody TestDto message) {
        Stream.of(connectionList.first(), connectionList.second())
                .forEach(connection ->
                        wrapper.run(connection.vhost(), () -> template.convertAndSend(connection.queue(), message)));
    }

}
