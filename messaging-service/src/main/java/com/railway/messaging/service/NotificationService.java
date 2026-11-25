
package com.railway.messaging.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.railway.messaging.config.RabbitMQConfig;
import com.railway.messaging.dto.NotificationMessage;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public NotificationService(RabbitTemplate rabbitTemplate,
                               ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendBookingNotification(NotificationMessage notification) {
        sendMessage(
                RabbitMQConfig.BOOKING_ROUTING_KEY,
                notification
        );
    }

    public void sendPaymentNotification(NotificationMessage notification) {
        sendMessage(
                RabbitMQConfig.PAYMENT_ROUTING_KEY,
                notification
        );
    }

    private void sendMessage(String routingKey,
                             NotificationMessage notification) {

        try {
            String message = objectMapper.writeValueAsString(notification);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    routingKey,
                    message
            );

            System.out.println("Notification published: " + routingKey);

        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Unable to convert notification to JSON", exception
            );
        }
    }
}
