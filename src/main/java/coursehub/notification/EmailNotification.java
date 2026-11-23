package coursehub.notification;

public record EmailNotification(String recipient, String subject, String body) implements Notification {
}
