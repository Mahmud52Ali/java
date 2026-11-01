package coursehub.demo;

import coursehub.model.Course;
import coursehub.model.CourseStatistics;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.OptionalLong;
import java.util.stream.Collector;

public final class Task06CustomCollector {
    private Task06CustomCollector() {
    }

    public static void run() {
        List<Course> courses = CourseExamples.catalog();
        Collector<Course, Accumulator, CourseStatistics> collector = Collector.of(
                Accumulator::new,
                Accumulator::add,
                Accumulator::combine,
                Accumulator::finish
        );

        CourseStatistics custom = courses.parallelStream().collect(collector);
        LongSummaryStatistics standard = courses.stream()
                .mapToLong(course -> course.duration().hours())
                .summaryStatistics();
        CourseStatistics expected = new CourseStatistics(
                standard.getCount(),
                standard.getSum(),
                standard.getCount() == 0 ? OptionalLong.empty() : OptionalLong.of(standard.getMin()),
                standard.getCount() == 0 ? OptionalLong.empty() : OptionalLong.of(standard.getMax())
        );

        System.out.println("\nЗадание 6. Собственный collector");
        System.out.println("Собственная статистика: " + custom);
        System.out.println("Совпадает с готовым summarizing collector: " + custom.equals(expected));
    }

    private static final class Accumulator {
        private long count;
        private long sum;
        private long min = Long.MAX_VALUE;
        private long max = Long.MIN_VALUE;

        private void add(Course course) {
            long hours = course.duration().hours();
            count++;
            sum = Math.addExact(sum, hours);
            min = Math.min(min, hours);
            max = Math.max(max, hours);
        }

        private Accumulator combine(Accumulator other) {
            count = Math.addExact(count, other.count);
            sum = Math.addExact(sum, other.sum);
            min = Math.min(min, other.min);
            max = Math.max(max, other.max);
            return this;
        }

        private CourseStatistics finish() {
            if (count == 0) {
                return new CourseStatistics(0, 0, OptionalLong.empty(), OptionalLong.empty());
            }
            return new CourseStatistics(count, sum, OptionalLong.of(min), OptionalLong.of(max));
        }
    }
}
