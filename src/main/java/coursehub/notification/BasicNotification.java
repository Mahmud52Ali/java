package coursehub.notification;

public record BasicNotification(String recipient, String body) implements Notification {
}
