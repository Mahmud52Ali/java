package coursehub.demo;

import coursehub.enrollment.Accepted;
import coursehub.enrollment.EnrollmentResult;
import coursehub.enrollment.Rejected;
import coursehub.enrollment.ResultDescriptions;
import coursehub.enrollment.WaitListed;

import java.util.List;
import java.util.Set;

public final class Task15EnrollmentEngine {
    private Task15EnrollmentEngine() {
    }

    public static void run() {
        System.out.println("\nЗадание 15. Расширяемый движок правил");
        EnrollmentRequest request = new EnrollmentRequest(20, Set.of("java-basics"), Set.of("java-basics"),
                2, 1, true, true);
        EnrollmentRule allRules = RuleCompositions.allOf(
                new MinimumAgeRule(18),
                new PrerequisiteRule(),
                new CapacityRule(),
                new PaymentRule(),
                new RegistrationWindowRule());
        EnrollmentService engine = new EnrollmentService(allRules);
        EnrollmentResult allOfResult = engine.enroll(request);
        System.out.println("allOf: " + ResultDescriptions.describe(allOfResult));

        EnrollmentRule anyRule = RuleCompositions.anyOf(new MinimumAgeRule(18), new PaymentRule());
        System.out.println("anyOf: " + ResultDescriptions.describe(new EnrollmentService(anyRule).enroll(request)));
        System.out.println("EnrollmentService принимает EnrollmentRule и возвращает EnrollmentResult без проверки классов.");
        System.out.println("Пять открытых правил: age, prerequisites, capacity, payment, registration window.");
        System.out.println("Диаграмма зависимостей: docs/enrollment-engine.md");
    }

    private record EnrollmentRequest(int age, Set<String> completedPrerequisites, Set<String> requiredPrerequisites,
                                     int capacity, int enrolled, boolean paymentComplete,
                                     boolean registrationOpen) {
    }

    private interface EnrollmentRule {
        EnrollmentResult evaluate(EnrollmentRequest request);
    }

    private static final class MinimumAgeRule implements EnrollmentRule {
        private final int minimumAge;

        private MinimumAgeRule(int minimumAge) {
            this.minimumAge = minimumAge;
        }

        @Override
        public EnrollmentResult evaluate(EnrollmentRequest request) {
            return request.age() >= minimumAge ? new Accepted("age requirement met")
                    : new Rejected("minimum age is " + minimumAge);
        }
    }

    private static final class PrerequisiteRule implements EnrollmentRule {
        @Override
        public EnrollmentResult evaluate(EnrollmentRequest request) {
            return request.completedPrerequisites().containsAll(request.requiredPrerequisites())
                    ? new Accepted("prerequisites completed")
                    : new Rejected("prerequisite missing");
        }
    }

    private static final class CapacityRule implements EnrollmentRule {
        @Override
        public EnrollmentResult evaluate(EnrollmentRequest request) {
            return request.enrolled() < request.capacity() ? new Accepted("place available")
                    : new WaitListed("course is full");
        }
    }

    private static final class PaymentRule implements EnrollmentRule {
        @Override
        public EnrollmentResult evaluate(EnrollmentRequest request) {
            return request.paymentComplete() ? new Accepted("payment complete")
                    : new Rejected("payment is required");
        }
    }

    private static final class RegistrationWindowRule implements EnrollmentRule {
        @Override
        public EnrollmentResult evaluate(EnrollmentRequest request) {
            return request.registrationOpen() ? new Accepted("registration is open")
                    : new Rejected("registration is closed");
        }
    }

    private static final class RuleCompositions {
        private static EnrollmentRule allOf(EnrollmentRule... rules) {
            List<EnrollmentRule> configured = List.of(rules);
            return request -> {
                for (EnrollmentRule rule : configured) {
                    EnrollmentResult result = rule.evaluate(request);
                    if (result.decision() != EnrollmentResult.Decision.ACCEPTED) {
                        return result;
                    }
                }
                return new Accepted("all rules passed");
            };
        }

        private static EnrollmentRule anyOf(EnrollmentRule... rules) {
            List<EnrollmentRule> configured = List.of(rules);
            return request -> {
                EnrollmentResult waitListed = null;
                for (EnrollmentRule rule : configured) {
                    EnrollmentResult result = rule.evaluate(request);
                    if (result.decision() == EnrollmentResult.Decision.ACCEPTED) {
                        return new Accepted("at least one rule passed");
                    }
                    if (result.decision() == EnrollmentResult.Decision.WAIT_LISTED) {
                        waitListed = result;
                    }
                }
                return waitListed == null ? new Rejected("no rule passed") : waitListed;
            };
        }
    }

    private static final class EnrollmentService {
        private final EnrollmentRule rule;

        private EnrollmentService(EnrollmentRule rule) {
            this.rule = rule;
        }

        private EnrollmentResult enroll(EnrollmentRequest request) {
            return rule.evaluate(request);
        }
    }
}
