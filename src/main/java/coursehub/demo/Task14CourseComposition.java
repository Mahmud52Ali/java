package coursehub.demo;

public final class Task14CourseComposition {
    private Task14CourseComposition() {
    }

    public static void run() {
        System.out.println("\nЗадание 14. Наследование взрывает комбинации");
        CourseOffer[] offers = {
                new CourseOffer("Java", new OnlineFormat(), new FixedPayment(120)),
                new CourseOffer("Databases", new ClassroomFormat(), new FreePayment()),
                new CourseOffer("Design", new HybridFormat(), new SubscriptionPayment(30)),
                new CourseOffer("Security", new MentorLedFormat(), new PerLessonPayment(15))
        };
        for (CourseOffer offer : offers) {
            System.out.println(offer.describe());
        }
        System.out.println("Формат и оплата независимы: новая реализация добавляется по одной оси.");
    }

    private interface CourseFormat {
        String label();
    }

    private static final class OnlineFormat implements CourseFormat {
        @Override
        public String label() {
            return "online";
        }
    }

    private static final class ClassroomFormat implements CourseFormat {
        @Override
        public String label() {
            return "classroom";
        }
    }

    private static final class HybridFormat implements CourseFormat {
        @Override
        public String label() {
            return "hybrid";
        }
    }

    private static final class MentorLedFormat implements CourseFormat {
        @Override
        public String label() {
            return "mentor-led";
        }
    }

    private interface PaymentPolicy {
        int amount();

        String label();
    }

    private static final class FreePayment implements PaymentPolicy {
        @Override
        public int amount() {
            return 0;
        }

        @Override
        public String label() {
            return "free";
        }
    }

    private record FixedPayment(int amount) implements PaymentPolicy {
        @Override
        public String label() {
            return "fixed";
        }
    }

    private record SubscriptionPayment(int amount) implements PaymentPolicy {
        @Override
        public String label() {
            return "subscription/month";
        }
    }

    private record PerLessonPayment(int amount) implements PaymentPolicy {
        @Override
        public String label() {
            return "per-lesson";
        }
    }

    private record CourseOffer(String title, CourseFormat format, PaymentPolicy payment) {
        private String describe() {
            return title + ": " + format.label() + ", " + payment.label() + ", " + payment.amount();
        }
    }
}
