package com.example.lab4.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "grade-queue";
    public static final String DLQ_NAME = "grade-queue.dlq";
    public static final String DLX_NAME = "grade.dlx";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    // if a message fails (throws an exception), it's sent to a specific DLQ instead of being lost
    // Dead Letter Exchange
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_NAME);
    }

    // Dead Letter Queue
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DLQ_NAME);
    }

    // bind DLQ to DLX
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("deadLetter");
    }

    // main queue (linked to DLX)
    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", "deadLetter")
                .build();
    }
}


//Main Queue (Message fails here)
//
//↓ Automatic Forwarding defined by x-dead-letter-exchange
//
//DLX (Router receives it)
//
//↓ Routing defined by Binding
//
//DLQ (Message lands here for later inspection)

//package com.example.lab4.config;
//
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//// deserialize the JSON message
//@Configuration
//public class RabbitConfig {
//    @Bean
//    public Jackson2JsonMessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//}