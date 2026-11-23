package coursehub.demo;

import coursehub.notification.BasicNotification;
import coursehub.notification.ConsoleSender;
import coursehub.notification.LoggingNotificationSender;
import coursehub.notification.MetricsNotificationSender;
import coursehub.notification.NotificationSender;

import java.util.ArrayList;
import java.util.List;

public final class Task07NotificationDecorators {
    private Task07NotificationDecorators() {
    }

    public static void run() {
        System.out.println("\nЗадание 7. Композиция вместо наследования");
        List<String> logThenMetricsEvents = new ArrayList<>();
        NotificationSender logThenMetrics = new LoggingNotificationSender(
                new MetricsNotificationSender(new ConsoleSender(), logThenMetricsEvents), logThenMetricsEvents);
        logThenMetrics.send(new BasicNotification("student-1", "First order"));
        System.out.println("Logging(Metrics(Sender)): " + logThenMetricsEvents);

        List<String> metricsThenLogEvents = new ArrayList<>();
        NotificationSender metricsThenLog = new MetricsNotificationSender(
                new LoggingNotificationSender(new ConsoleSender(), metricsThenLogEvents), metricsThenLogEvents);
        metricsThenLog.send(new BasicNotification("student-2", "Second order"));
        System.out.println("Metrics(Logging(Sender)): " + metricsThenLogEvents);
        System.out.println("Декораторы добавляют поведения без подкласса для каждой комбинации.");
    }
}
