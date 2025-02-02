package org.example.sample.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.connections")
public record ConnectionList(ConnectionProperty first, ConnectionProperty second) {

    public record ConnectionProperty(String vhost, String queue) {
    }

}
