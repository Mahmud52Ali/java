package coursehub.demo;

import coursehub.model.Registration;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

public class Task04Waitlist {

    public static void run() {
        System.out.println("\nЗадание 4. Очередь ожидания");

        Queue<Registration> waitlist = new ArrayDeque<>();

        waitlist.offer(new Registration(1, 101));
        waitlist.offer(new Registration(2, 101));
        waitlist.add(new Registration(3, 101));

        System.out.println("Очередь: " + waitlist);

        System.out.println("\npeek(): " + waitlist.peek());
        System.out.println("element(): " + waitlist.element());
        System.out.println("Размер после просмотра: " + waitlist.size());

        System.out.println("\npoll(): " + waitlist.poll());
        System.out.println("remove(): " + waitlist.remove());

        System.out.println("Осталась очередь: " + waitlist);

        System.out.println("poll(): " + waitlist.poll());

        System.out.println("\nПустая очередь:");
        System.out.println("peek(): " + waitlist.peek());
        System.out.println("poll(): " + waitlist.poll());

        try {
            waitlist.element();
        } catch (NoSuchElementException exception) {
            System.out.println("element(): исключение — очередь пуста");
        }

        try {
            waitlist.remove();
        } catch (NoSuchElementException exception) {
            System.out.println("remove(): исключение — очередь пуста");
        }
    }
}
