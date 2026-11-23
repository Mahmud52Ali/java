package coursehub.notification;

public interface NotificationSender {
    void send(Notification notification);

    default void send(String recipient, String body) {
        send(new BasicNotification(recipient, body));
    }
}
