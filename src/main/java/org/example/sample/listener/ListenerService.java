package org.example.sample.listener;

import lombok.extern.log4j.Log4j2;
import org.example.sample.model.TestDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ListenerService {

    @RabbitListener(queues = "${rabbitmq.connections.first.queue}", containerFactory = "factory1")
    public void listen1(@Payload TestDto in) {
        log.info("Received q1 <{}>", in.toString());
    }

    @RabbitListener(queues = "${rabbitmq.connections.second.queue}", containerFactory = "factory2")
    public void listen2(@Payload TestDto in) {
        log.info("Received q2 <{}>", in.toString());
    }
}
