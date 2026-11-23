package coursehub.notification;

public class ConsoleSender implements NotificationSender {
    @Override
    public void send(Notification notification) {
        System.out.println("CONSOLE to " + notification.recipient() + ": " + notification.body());
    }
}
