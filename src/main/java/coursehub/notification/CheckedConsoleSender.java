package coursehub.notification;

public class CheckedConsoleSender extends AbstractNotificationSender {
    @Override
    protected void doSend(Notification notification) {
        System.out.println("Validated console message to " + notification.recipient() + ": " + notification.body());
    }
}
