package org.example.sample.configuration;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.sample.model.ConnectionList;
import org.example.sample.model.RabbitProperty;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactoryContextWrapper;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)

public class RabbitConfiguration {

    final ConnectionList connectionList;

    final RabbitProperty rabbitProperty;

    /// Define both connection factories
    /// I use CacheConnectionFactory as we have same instance of RabbitMQ running on localhost,
    /// swappable with ConnectionFactory
    @Bean
    CachingConnectionFactory firstConnectionFactory() {
        var factory = new CachingConnectionFactory(rabbitProperty.host());
        factory.setPort(rabbitProperty.port());
        factory.setUsername(rabbitProperty.username());
        factory.setPassword(rabbitProperty.password());
        return factory;
    }

    @Bean
    CachingConnectionFactory secondConnectionFactory() {
        var factory = new CachingConnectionFactory(rabbitProperty.host());
        factory.setPort(rabbitProperty.port());
        factory.setUsername(rabbitProperty.username());
        factory.setPassword(rabbitProperty.password());
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
                        .collect(Collectors.toMap(ConnectionFactory::getVirtualHost, v -> v))
        );

        return rcf;
    }

    /// Use JSON for working with listeners
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
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

    ///  These both beans are used to activate our listeners if we exclude rabbit autoconfiguration
//    @Bean
//    @Primary
//    public RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry() {
//        return new RabbitListenerEndpointRegistry();
//    }
//
//    @Bean
//    public RabbitListenerAnnotationBeanPostProcessor postProcessor(RabbitListenerEndpointRegistry registry) {
//        MultiRabbitListenerAnnotationBeanPostProcessor postProcessor
//                = new MultiRabbitListenerAnnotationBeanPostProcessor();
//        postProcessor.setEndpointRegistry(registry);
//        postProcessor.setContainerFactoryBeanName("defaultContainerFactory");
//        return postProcessor;
//    }

    ///  Also add jacksonConverter to receive data in JSON
    @Bean
    public SimpleRabbitListenerContainerFactory factory1(CachingConnectionFactory firstConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(firstConnectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory factory2(CachingConnectionFactory secondConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(secondConnectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }

    /// Also define sending type (JSON)
    @Bean
    RabbitTemplate template(SimpleRoutingConnectionFactory rcf) {
        var template = new RabbitTemplate(rcf);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }

    /// This bean is used to wrap the connection factory
    /// It is used to switch between connection factories
    @Bean
    ConnectionFactoryContextWrapper wrapper(SimpleRoutingConnectionFactory rcf) {
        return new ConnectionFactoryContextWrapper(rcf);
    }

}
