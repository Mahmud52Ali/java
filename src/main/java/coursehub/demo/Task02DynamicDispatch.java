package coursehub.demo;

import coursehub.notification.ConsoleSender;
import coursehub.notification.EmailNotification;
import coursehub.notification.EmailSender;
import coursehub.notification.Notification;
import coursehub.notification.NotificationSender;

public final class Task02DynamicDispatch {
    private Task02DynamicDispatch() {
    }

    public static void run() {
        System.out.println("\nЗадание 2. Dynamic dispatch на бумаге");
        EmailNotification email = new EmailNotification("student@example.test", "Welcome", "Course starts Monday");
        NotificationSender baseReference = new EmailSender();
        baseReference.send(email);
        EmailSender emailReference = new EmailSender();
        emailReference.send(email);
        emailReference.send((Notification) email);
        NotificationSender consoleReference = new ConsoleSender();
        consoleReference.send(email);
        System.out.println("Перегрузка выбирается по типу ссылки и аргумента; override выбирается по реальному классу объекта.");
    }
}
