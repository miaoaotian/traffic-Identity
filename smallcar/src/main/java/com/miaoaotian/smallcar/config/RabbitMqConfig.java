package com.miaoaotian.smallcar.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
@Configuration
public class RabbitMqConfig {
    public static final String TOPIC_EXCHANGE_NAME = "table_changes_exchange";

    public static final String DRIVE_TYPE_QUEUE_NAME = "drive_type_queue";
    public static final String OBJECTS_QUEUE_NAME = "objects_queue";

    public static final String DRIVE_TYPE_ROUTING_KEY = "drive_type_key";
    public static final String OBJECTS_ROUTING_KEY = "objects_key";

    public static final String TRAFFIC_SIGNAL_QUEUE_NAME = "traffic_signal_queue";
    public static final String TRAFFIC_SIGNAL_ROUTING_KEY = "traffic_signal_key";

    @Bean
    Queue driveTypeQueue() {
        return new Queue(DRIVE_TYPE_QUEUE_NAME, true);
    }

    @Bean
    Queue objectsQueue() {
        return new Queue(OBJECTS_QUEUE_NAME, true);
    }

    @Bean
    Queue trafficSignalQueue() { return new Queue(TRAFFIC_SIGNAL_QUEUE_NAME, true);}

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    Binding driveTypeBinding(Queue driveTypeQueue, TopicExchange exchange) {
        return BindingBuilder.bind(driveTypeQueue).to(exchange).with(DRIVE_TYPE_ROUTING_KEY);
    }

    @Bean
    Binding objectsBinding(Queue objectsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(objectsQueue).to(exchange).with(OBJECTS_ROUTING_KEY);
    }

    @Bean
    Binding trafficSignalBinding(Queue trafficSignalQueue, TopicExchange exchange) {
        return BindingBuilder.bind(trafficSignalQueue).to(exchange).with(TRAFFIC_SIGNAL_ROUTING_KEY);
    }
}
