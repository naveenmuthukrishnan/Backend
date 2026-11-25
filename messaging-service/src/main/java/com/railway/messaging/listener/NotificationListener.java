
package com.railway.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.railway.messaging.config.RabbitMQConfig;
import com.railway.messaging.dto.NotificationMessage;

@Component
public class NotificationListener {

    private final ObjectMapper objectMapper;

    public NotificationListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.BOOKING_QUEUE)
    public void receiveBookingNotification(String message) throws Exception {
        NotificationMessage notification =
                objectMapper.readValue(message, NotificationMessage.class);

        displayNotification(notification, "BOOKING");
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE)
    public void receivePaymentNotification(String message) throws Exception {
        NotificationMessage notification =
                objectMapper.readValue(message, NotificationMessage.class);

        displayNotification(notification, "PAYMENT");
    }

    private void displayNotification(NotificationMessage notification, String type) {
        System.out.println("\n========== " + type + " NOTIFICATION ==========");
        System.out.println("Recipient: " + notification.getRecipientEmail());
        System.out.println("Subject: " + notification.getSubject());
        System.out.println("Message: " + notification.getMessage());
        System.out.println("========================================\n");
    }
}
