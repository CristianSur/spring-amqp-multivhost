package org.example.sample.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq")
public record RabbitProperty(String host, Integer port, String username, String password) {
}
