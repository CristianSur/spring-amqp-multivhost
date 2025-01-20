package org.example.sample;

import org.example.sample.model.ConnectionList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties({ConnectionList.class})
public class SpringAmqpMultivhostApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAmqpMultivhostApplication.class, args);
    }

}
