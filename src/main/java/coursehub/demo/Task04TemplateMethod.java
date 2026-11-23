package coursehub.demo;

import coursehub.notification.CheckedConsoleSender;
import coursehub.notification.CheckedEmailSender;
import coursehub.notification.EmailNotification;
import coursehub.notification.Notification;
import coursehub.notification.NotificationSender;

public final class Task04TemplateMethod {
    private Task04TemplateMethod() {
    }

    public static void run() {
        System.out.println("\nЗадание 4. Абстрактный базовый класс");
        Notification notification = new EmailNotification("student@example.test", "Reminder", "Submit the project");
        NotificationSender[] senders = {new CheckedEmailSender(), new CheckedConsoleSender()};
        for (NotificationSender sender : senders) {
            sender.send(notification);
        }
        System.out.println("send final: проверка общая; doSend остаётся точкой расширения.");
    }
}
