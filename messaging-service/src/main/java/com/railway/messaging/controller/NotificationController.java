
package com.railway.messaging.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.railway.messaging.dto.NotificationMessage;
import com.railway.messaging.service.NotificationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Value("${internal.auth.api-key}")
    private String internalApiKey;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @PostMapping("/booking")
    public ResponseEntity<String> sendBooking(
            @RequestHeader("X-INTERNAL-API-KEY") String apiKey,
            @Valid @RequestBody NotificationRequest request) {

        validateApiKey(apiKey);

        NotificationMessage message = new NotificationMessage(
                request.recipientEmail(),
                "Railway Booking Notification",
                request.message(),
                "BOOKING"
        );

        notificationService.sendBookingNotification(message);

        return ResponseEntity.ok(
                "Booking notification published"
        );
    }

    @PostMapping("/payment")
    public ResponseEntity<String> sendPayment(
            @RequestHeader("X-INTERNAL-API-KEY") String apiKey,
            @Valid @RequestBody NotificationRequest request) {

        validateApiKey(apiKey);

        NotificationMessage message = new NotificationMessage(
                request.recipientEmail(),
                "Railway Payment Notification",
                request.message(),
                "PAYMENT"
        );

        notificationService.sendPaymentNotification(message);

        return ResponseEntity.ok(
                "Payment notification published"
        );
    }

    private void validateApiKey(String apiKey) {

        if (apiKey == null ||
                internalApiKey == null ||
                !MessageDigest.isEqual(
                        internalApiKey.getBytes(StandardCharsets.UTF_8),
                        apiKey.getBytes(StandardCharsets.UTF_8))) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid internal API key"
            );
        }
    }

    public record NotificationRequest(

            @NotBlank
            @Email
            String recipientEmail,

            @NotBlank
            String message

    ) {}
}
