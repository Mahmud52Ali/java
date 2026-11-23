package coursehub.demo;

import java.util.List;
import java.util.Set;

public final class Task09EnrollmentStrategy {
    private Task09EnrollmentStrategy() {
    }

    public static void run() {
        System.out.println("\nЗадание 9. Политика допуска как стратегия");
        EnrollmentRequest request = new EnrollmentRequest(20, Set.of("java-basics"), Set.of("java-basics"), 20, 12, 3);
        EnrollmentPolicy combined = new AllOfPolicy(List.of(
                new MinimumAgePolicy(18), new PrerequisitePolicy(), new CapacityPolicy()));
        EnrollmentService service = new EnrollmentService(combined);
        System.out.println("Combined policy: " + service.check(request));

        EnrollmentService newPolicyService = new EnrollmentService(new CourseLoadPolicy(4));
        System.out.println("New policy, same service class: " + newPolicyService.check(request));
    }

    private record EnrollmentRequest(int age, Set<String> completed, Set<String> required,
                                     int capacity, int enrolled, int currentCourseLoad) {
    }

    private record PolicyResult(boolean allowed, String reason) {
        @Override
        public String toString() {
            return allowed ? "allowed" : "rejected: " + reason;
        }
    }

    private interface EnrollmentPolicy {
        PolicyResult check(EnrollmentRequest request);
    }

    private static final class MinimumAgePolicy implements EnrollmentPolicy {
        private final int minimumAge;

        private MinimumAgePolicy(int minimumAge) {
            this.minimumAge = minimumAge;
        }

        @Override
        public PolicyResult check(EnrollmentRequest request) {
            return request.age() >= minimumAge ? new PolicyResult(true, "")
                    : new PolicyResult(false, "age below " + minimumAge);
        }
    }

    private static final class PrerequisitePolicy implements EnrollmentPolicy {
        @Override
        public PolicyResult check(EnrollmentRequest request) {
            return request.completed().containsAll(request.required()) ? new PolicyResult(true, "")
                    : new PolicyResult(false, "prerequisite is missing");
        }
    }

    private static final class CapacityPolicy implements EnrollmentPolicy {
        @Override
        public PolicyResult check(EnrollmentRequest request) {
            return request.enrolled() < request.capacity() ? new PolicyResult(true, "")
                    : new PolicyResult(false, "course is full");
        }
    }

    private static final class CourseLoadPolicy implements EnrollmentPolicy {
        private final int maximumLoad;

        private CourseLoadPolicy(int maximumLoad) {
            this.maximumLoad = maximumLoad;
        }

        @Override
        public PolicyResult check(EnrollmentRequest request) {
            return request.currentCourseLoad() < maximumLoad ? new PolicyResult(true, "")
                    : new PolicyResult(false, "student course load is full");
        }
    }

    private static final class AllOfPolicy implements EnrollmentPolicy {
        private final List<EnrollmentPolicy> policies;

        private AllOfPolicy(List<EnrollmentPolicy> policies) {
            this.policies = policies;
        }

        @Override
        public PolicyResult check(EnrollmentRequest request) {
            for (EnrollmentPolicy policy : policies) {
                PolicyResult result = policy.check(request);
                if (!result.allowed()) {
                    return result;
                }
            }
            return new PolicyResult(true, "");
        }
    }

    private static final class EnrollmentService {
        private final EnrollmentPolicy policy;

        private EnrollmentService(EnrollmentPolicy policy) {
            this.policy = policy;
        }

        private PolicyResult check(EnrollmentRequest request) {
            return policy.check(request);
        }
    }
}
