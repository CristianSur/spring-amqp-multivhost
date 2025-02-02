package org.example.sample;

import org.example.sample.model.ConnectionList;
import org.example.sample.model.RabbitProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@EnableConfigurationProperties({ConnectionList.class, RabbitProperty.class})
//@SpringBootApplication(exclude = RabbitAutoConfiguration.class)
@SpringBootApplication
public class SpringAmqpMultivhostApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAmqpMultivhostApplication.class, args);
    }

}
