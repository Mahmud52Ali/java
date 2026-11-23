package coursehub.notification;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MetricsNotificationSender implements NotificationSender {
    private final NotificationSender delegate;
    private final List<String> events;
    private final AtomicInteger sentCount = new AtomicInteger();

    public MetricsNotificationSender(NotificationSender delegate, List<String> events) {
        this.delegate = delegate;
        this.events = events;
    }

    @Override
    public void send(Notification notification) {
        events.add("metrics:start");
        delegate.send(notification);
        events.add("metrics:sent=" + sentCount.incrementAndGet());
    }
}
