package org.example.sample.configuration;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.sample.model.ConnectionList;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactoryContextWrapper;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RabbitConfiguration {

    @Value("${spring.rabbitmq.host}")
    String host;

    final ConnectionList connectionList;

    /// Define both connection factories
    /// I use CacheConnectionFactory as we have same instance of RabbitMQ running on localhost,
    /// swappable with ConnectionFactory
    @Bean
    CachingConnectionFactory firstConnectionFactory() {
        var factory = new CachingConnectionFactory(host);
        factory.setVirtualHost(connectionList.first().vhost());
        return factory;
    }

    @Bean
    CachingConnectionFactory secondConnectionFactory() {
        var factory = new CachingConnectionFactory(host);
        factory.setVirtualHost(connectionList.second().vhost());
        return factory;
    }

    /// This bean is our main connection factory, containing both connection factories
    @Bean
    @Primary
    SimpleRoutingConnectionFactory rcf(CachingConnectionFactory firstConnectionFactory, CachingConnectionFactory secondConnectionFactory) {
        SimpleRoutingConnectionFactory rcf = new SimpleRoutingConnectionFactory();
        rcf.setDefaultTargetConnectionFactory(firstConnectionFactory);
        rcf.setTargetConnectionFactories(
                Stream.of(firstConnectionFactory, secondConnectionFactory)
                        .collect(Collectors.toMap(CachingConnectionFactory::getVirtualHost, v -> v))
        );

        return rcf;
    }

    /// Define RabbitAdmin beans for both connection factories
    /// RabbitAdmin is used to declare queues, exchanges, bindings, etc.
    /// Name of the bean should be factoryName + "-admin"
    @Bean("factory1-admin")
    RabbitAdmin admin1(CachingConnectionFactory firstConnectionFactory) {
        return new RabbitAdmin(firstConnectionFactory);
    }

    @Bean("factory2-admin")
    RabbitAdmin admin2(CachingConnectionFactory secondConnectionFactory) {
        return new RabbitAdmin(secondConnectionFactory);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory factory1(CachingConnectionFactory firstConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(firstConnectionFactory);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory factory2(CachingConnectionFactory secondConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(secondConnectionFactory);
        return factory;
    }

    @Bean
    RabbitTemplate template(SimpleRoutingConnectionFactory rcf) {
        return new RabbitTemplate(rcf);
    }

    /// This bean is used to wrap the connection factory
    /// It is used to switch between connection factories
    @Bean
    ConnectionFactoryContextWrapper wrapper(SimpleRoutingConnectionFactory rcf) {
        return new ConnectionFactoryContextWrapper(rcf);
    }


}
