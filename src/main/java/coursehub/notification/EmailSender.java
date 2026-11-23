package coursehub.notification;

public class EmailSender implements NotificationSender {
    @Override
    public void send(Notification notification) {
        System.out.println("EMAIL to " + notification.recipient() + ": " + notification.body());
    }

    public void send(EmailNotification notification) {
        System.out.println("EMAIL subject='" + notification.subject() + "' to "
                + notification.recipient() + ": " + notification.body());
    }
}
