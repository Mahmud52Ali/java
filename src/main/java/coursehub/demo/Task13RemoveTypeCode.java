package coursehub.demo;

import coursehub.notification.BasicNotification;
import coursehub.notification.ConsoleSender;
import coursehub.notification.EmailSender;
import coursehub.notification.Notification;
import coursehub.notification.NotificationSender;
import coursehub.notification.NotificationService;

public final class Task13RemoveTypeCode {
    private Task13RemoveTypeCode() {
    }

    public static void run() {
        System.out.println("\nЗадание 13. Удаление type code");
        Notification notification = new BasicNotification("student-13", "Enrollment was accepted");
        NotificationSender[] senders = {new EmailSender(), new ConsoleSender(), new PushSender()};
        for (NotificationSender sender : senders) {
            sender.send(notification);
        }
        NotificationService service = new NotificationService(new ConsoleSender());
        service.notifyRecipient("student-13", "A class client depends on NotificationSender.");
        System.out.println("Разные каналы реализуют поведение; отдельный класс нужен только при отличающемся поведении.");
    }

    private static final class PushSender implements NotificationSender {
        @Override
        public void send(Notification notification) {
            System.out.println("PUSH to " + notification.recipient() + ": " + notification.body());
        }
    }
}
