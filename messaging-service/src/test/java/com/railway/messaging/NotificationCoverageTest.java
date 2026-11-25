
// package com.railway.messaging;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.railway.messaging.config.RabbitMQConfig;
// import com.railway.messaging.controller.NotificationController;
// import com.railway.messaging.dto.NotificationMessage;
// import com.railway.messaging.listener.NotificationListener;
// import com.railway.messaging.service.NotificationService;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import org.springframework.amqp.core.Binding;
// import org.springframework.amqp.core.DirectExchange;
// import org.springframework.amqp.core.Queue;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.http.ResponseEntity;

// import java.util.concurrent.atomic.AtomicReference;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// import org.mockito.ArgumentCaptor;

// class NotificationCoverageTest {

//     private RabbitTemplate rabbitTemplate;
//     private ObjectMapper objectMapper;
//     private NotificationService notificationService;

//     @BeforeEach
//     void setUp() {
//         rabbitTemplate = mock(RabbitTemplate.class);
//         objectMapper = mock(ObjectMapper.class);

//         notificationService = new NotificationService(
//                 rabbitTemplate,
//                 objectMapper
//         );
//     }

//     // ---------------- NOTIFICATION SERVICE TESTS ----------------

//     @Test
//     void sendBookingNotification_Success() throws Exception {

//         NotificationMessage notification = new NotificationMessage(
//                 "passenger@gmail.com",
//                 "Booking Confirmation",
//                 "Your booking is confirmed",
//                 "BOOKING"
//         );

//         String json = "{\"recipientEmail\":\"passenger@gmail.com\"}";

//         when(objectMapper.writeValueAsString(notification))
//                 .thenReturn(json);

//         notificationService.sendBookingNotification(notification);

//         verify(objectMapper).writeValueAsString(notification);

//         verify(rabbitTemplate).convertAndSend(
//                 RabbitMQConfig.EXCHANGE,
//                 RabbitMQConfig.BOOKING_ROUTING_KEY,
//                 json
//         );
//     }

//     @Test
//     void sendPaymentNotification_Success() throws Exception {

//         NotificationMessage notification = new NotificationMessage(
//                 "passenger@gmail.com",
//                 "Payment Confirmation",
//                 "Payment successful",
//                 "PAYMENT"
//         );

//         String json = "{\"notificationType\":\"PAYMENT\"}";

//         when(objectMapper.writeValueAsString(notification))
//                 .thenReturn(json);

//         notificationService.sendPaymentNotification(notification);

//         verify(rabbitTemplate).convertAndSend(
//                 RabbitMQConfig.EXCHANGE,
//                 RabbitMQConfig.PAYMENT_ROUTING_KEY,
//                 json
//         );
//     }

//     @Test
//     void sendBookingNotification_JsonConversionFailure() throws Exception {

//         NotificationMessage notification = new NotificationMessage(
//                 "passenger@gmail.com",
//                 "Booking",
//                 "Booking message",
//                 "BOOKING"
//         );

//         when(objectMapper.writeValueAsString(notification))
//                 .thenThrow(new JsonProcessingException("JSON error") {});

//         assertThrows(
//                 IllegalArgumentException.class,
//                 () -> notificationService.sendBookingNotification(notification)
//         );

//         verifyNoInteractions(rabbitTemplate);
//     }

//     @Test
//     void sendPaymentNotification_JsonConversionFailure() throws Exception {

//         NotificationMessage notification = new NotificationMessage(
//                 "passenger@gmail.com",
//                 "Payment",
//                 "Payment message",
//                 "PAYMENT"
//         );

//         when(objectMapper.writeValueAsString(notification))
//                 .thenThrow(new JsonProcessingException("JSON error") {});

//         assertThrows(
//                 IllegalArgumentException.class,
//                 () -> notificationService.sendPaymentNotification(notification)
//         );

//         verifyNoInteractions(rabbitTemplate);
//     }

//     // ---------------- NOTIFICATION LISTENER TESTS ----------------

//     @Test
//     void receiveBookingNotification_Success() throws Exception {

//         ObjectMapper realMapper = new ObjectMapper();

//         NotificationListener listener =
//                 new NotificationListener(realMapper);

//         String json = """
//                 {
//                     "recipientEmail": "passenger@gmail.com",
//                     "subject": "Booking Confirmation",
//                     "message": "Your booking is confirmed",
//                     "notificationType": "BOOKING"
//                 }
//                 """;

//         assertDoesNotThrow(
//                 () -> listener.receiveBookingNotification(json)
//         );
//     }

//     @Test
//     void receivePaymentNotification_Success() throws Exception {

//         ObjectMapper realMapper = new ObjectMapper();

//         NotificationListener listener =
//                 new NotificationListener(realMapper);

//         String json = """
//                 {
//                     "recipientEmail": "passenger@gmail.com",
//                     "subject": "Payment Confirmation",
//                     "message": "Payment successful",
//                     "notificationType": "PAYMENT"
//                 }
//                 """;

//         assertDoesNotThrow(
//                 () -> listener.receivePaymentNotification(json)
//         );
//     }

//     @Test
//     void receiveBookingNotification_InvalidJson() {

//         NotificationListener listener =
//                 new NotificationListener(new ObjectMapper());

//         assertThrows(
//                 Exception.class,
//                 () -> listener.receiveBookingNotification("{invalid-json")
//         );
//     }

//     @Test
//     void receivePaymentNotification_InvalidJson() {

//         NotificationListener listener =
//                 new NotificationListener(new ObjectMapper());

//         assertThrows(
//                 Exception.class,
//                 () -> listener.receivePaymentNotification("{invalid-json")
//         );
//     }

//     // ---------------- NOTIFICATION CONTROLLER TESTS ----------------

//     @Test
//     void controllerSendBookingNotification_Success() {

//         NotificationService service = mock(NotificationService.class);

//         NotificationController controller =
//                 new NotificationController(service);

//         NotificationController.NotificationRequest request =
//                 new NotificationController.NotificationRequest(
//                         "passenger@gmail.com",
//                         "Your booking is confirmed"
//                 );

//         ResponseEntity<String> response =
//                 controller.sendBooking(request);

//         assertEquals(200, response.getStatusCode().value());
//         assertEquals(
//                 "Booking notification sent to RabbitMQ",
//                 response.getBody()
//         );

//         ArgumentCaptor<NotificationMessage> captor =
//                 ArgumentCaptor.forClass(NotificationMessage.class);

//         verify(service).sendBookingNotification(captor.capture());

//         NotificationMessage message = captor.getValue();

//         assertEquals("passenger@gmail.com", message.getRecipientEmail());
//         assertEquals("Railway Booking Notification", message.getSubject());
//         assertEquals("Your booking is confirmed", message.getMessage());
//         assertEquals("BOOKING", message.getNotificationType());
//     }

//     @Test
//    void controllerSendPaymentNotification_Success() {

//         NotificationService service = mock(NotificationService.class);

//         NotificationController controller =
//                 new NotificationController(service);

//         NotificationController.NotificationRequest request =
//                 new NotificationController.NotificationRequest(
//                         "passenger@gmail.com",
//                         "Your payment is successful"
//                 );

//         ResponseEntity<String> response =
//                 controller.sendPayment(request);

//         assertEquals(200, response.getStatusCode().value());
//         assertEquals(
//                 "Payment notification sent to RabbitMQ",
//                 response.getBody()
//         );

//         ArgumentCaptor<NotificationMessage> captor =
//                 ArgumentCaptor.forClass(NotificationMessage.class);

//         verify(service).sendPaymentNotification(captor.capture());

//         NotificationMessage message = captor.getValue();

//         assertEquals("passenger@gmail.com", message.getRecipientEmail());
//         assertEquals("Railway Payment Notification", message.getSubject());
//         assertEquals("Your payment is successful", message.getMessage());
//         assertEquals("PAYMENT", message.getNotificationType());
//     }

//     // ---------------- RABBITMQ CONFIGURATION TESTS ----------------

//     @Test
//     void railwayExchange_CreatesDurableExchange() {

//         RabbitMQConfig config = new RabbitMQConfig();

//         DirectExchange exchange = config.railwayExchange();

//         assertNotNull(exchange);
//         assertEquals(RabbitMQConfig.EXCHANGE, exchange.getName());
//         assertTrue(exchange.isDurable());
//     }

//     @Test
//     void bookingQueue_CreatesDurableQueue() {

//         RabbitMQConfig config = new RabbitMQConfig();

//         Queue queue = config.bookingQueue();

//         assertNotNull(queue);
//         assertEquals(RabbitMQConfig.BOOKING_QUEUE, queue.getName());
//         assertTrue(queue.isDurable());
//     }

//     @Test
//     void paymentQueue_CreatesDurableQueue() {

//         RabbitMQConfig config = new RabbitMQConfig();

//         Queue queue = config.paymentQueue();

//         assertNotNull(queue);
//         assertEquals(RabbitMQConfig.PAYMENT_QUEUE, queue.getName());
//         assertTrue(queue.isDurable());
//     }

//     @Test
//     void bookingBinding_CreatesCorrectBinding() {

//         RabbitMQConfig config = new RabbitMQConfig();

//         Binding binding = config.bookingBinding();

//         assertNotNull(binding);
//         assertEquals(
//                 RabbitMQConfig.BOOKING_ROUTING_KEY,
//                 binding.getRoutingKey()
//         );
//         assertEquals(
//                 RabbitMQConfig.EXCHANGE,
//                 binding.getExchange()
//         );
//         assertEquals(
//                 RabbitMQConfig.BOOKING_QUEUE,
//                 binding.getDestination()
//         );
//     }

//     @Test
//     void paymentBinding_CreatesCorrectBinding() {

//         RabbitMQConfig config = new RabbitMQConfig();

//         Binding binding = config.paymentBinding();

//         assertNotNull(binding);
//         assertEquals(
//                 RabbitMQConfig.PAYMENT_ROUTING_KEY,
//                 binding.getRoutingKey()
//         );
//         assertEquals(
//                 RabbitMQConfig.EXCHANGE,
//                 binding.getExchange()
//         );
//         assertEquals(
//                 RabbitMQConfig.PAYMENT_QUEUE,
//                 binding.getDestination()
//         );
//     }

//     // ---------------- NOTIFICATION DTO TESTS ----------------

//     @Test
//     void notificationMessage_AllArgsConstructorAndGetters() {

//         NotificationMessage message = new NotificationMessage(
//                 "passenger@gmail.com",
//                 "Booking",
//                 "Booking confirmed",
//                 "BOOKING"
//         );

//         assertEquals("passenger@gmail.com", message.getRecipientEmail());
//         assertEquals("Booking", message.getSubject());
//         assertEquals("Booking confirmed", message.getMessage());
//         assertEquals("BOOKING", message.getNotificationType());
//     }

//     @Test
//     void notificationMessage_NoArgsConstructorAndSetters() {

//         NotificationMessage message = new NotificationMessage();

//         message.setRecipientEmail("passenger@gmail.com");
//         message.setSubject("Payment");
//         message.setMessage("Payment successful");
//         message.setNotificationType("PAYMENT");

//         assertEquals("passenger@gmail.com", message.getRecipientEmail());
//         assertEquals("Payment", message.getSubject());
//         assertEquals("Payment successful", message.getMessage());
//         assertEquals("PAYMENT", message.getNotificationType());
//     }
// }
