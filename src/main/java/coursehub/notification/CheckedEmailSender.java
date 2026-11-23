package coursehub.notification;

public class CheckedEmailSender extends AbstractNotificationSender {
    @Override
    protected void doSend(Notification notification) {
        System.out.println("Validated email to " + notification.recipient() + ": " + notification.body());
    }
}
