package org.example.sample.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ListenerService {

    @RabbitListener(queues = "${rabbit-config.connections.first.queue}", containerFactory = "factory1")
    public void listen1(String in) {
        log.info("Received q1 <{}>", in);

    }

    @RabbitListener(queues = "${rabbit-config.connections.second.queue}", containerFactory = "factory2")
    public void listen2(String in) {
        log.info("Received q2 <{}>", in);

    }
}
