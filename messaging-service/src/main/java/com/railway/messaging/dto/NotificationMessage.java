
package com.railway.messaging.dto;

public class NotificationMessage {

    private String recipientEmail;
    private String subject;
    private String message;
    private String notificationType;

    public NotificationMessage() {
    }

    public NotificationMessage(String recipientEmail, String subject,
                               String message, String notificationType) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
        this.notificationType = notificationType;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
