package coursehub.notification;

import java.util.List;

public class LoggingNotificationSender implements NotificationSender {
    private final NotificationSender delegate;
    private final List<String> events;

    public LoggingNotificationSender(NotificationSender delegate, List<String> events) {
        this.delegate = delegate;
        this.events = events;
    }

    @Override
    public void send(Notification notification) {
        events.add("log:before");
        delegate.send(notification);
        events.add("log:after");
    }
}
