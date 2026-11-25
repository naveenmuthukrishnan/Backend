
package com.railway.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "railway.exchange";

    public static final String BOOKING_QUEUE = "booking.notification.queue";
    public static final String PAYMENT_QUEUE = "payment.notification.queue";

    public static final String BOOKING_ROUTING_KEY = "booking.notification";
    public static final String PAYMENT_ROUTING_KEY = "payment.notification";

    @Bean
    public DirectExchange railwayExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue bookingQueue() {
        return new Queue(BOOKING_QUEUE, true);
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    @Bean
    public Binding bookingBinding() {
        return BindingBuilder
                .bind(bookingQueue())
                .to(railwayExchange())
                .with(BOOKING_ROUTING_KEY);
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder
                .bind(paymentQueue())
                .to(railwayExchange())
                .with(PAYMENT_ROUTING_KEY);
    }
}
