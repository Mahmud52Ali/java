package coursehub.notification;

public class NotificationService {
    private final NotificationSender sender;

    public NotificationService(NotificationSender sender) {
        this.sender = sender;
    }

    public void notifyRecipient(String recipient, String message) {
        sender.send(recipient, message);
    }
}
