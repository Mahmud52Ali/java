package coursehub.demo;

import coursehub.enrollment.Accepted;
import coursehub.enrollment.EnrollmentResult;
import coursehub.enrollment.Rejected;
import coursehub.enrollment.ReviewRequired;
import coursehub.enrollment.ResultDescriptions;
import coursehub.enrollment.WaitListed;

import java.util.List;

public final class Task12SealedResult {
    private Task12SealedResult() {
    }

    public static void run() {
        System.out.println("\nЗадание 12. Sealed-иерархия результата");
        List<EnrollmentResult> results = List.of(
                new Accepted("places reserved"),
                new Rejected("prerequisite missing"),
                new WaitListed("course is full"),
                new ReviewRequired("manual review"));
        results.forEach(result -> System.out.println(ResultDescriptions.describe(result)));
        System.out.println("ReviewRequired добавлен в permits; исчерпывающий switch пришлось дополнить новой веткой.");
    }
}
