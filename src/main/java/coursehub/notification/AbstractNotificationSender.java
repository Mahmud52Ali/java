package coursehub.notification;

public abstract class AbstractNotificationSender implements NotificationSender {
    @Override
    public final void send(Notification notification) {
        if (notification == null || notification.recipient() == null || notification.recipient().isBlank()) {
            throw new IllegalArgumentException("Recipient is required");
        }
        doSend(notification);
    }

    protected abstract void doSend(Notification notification);
}
