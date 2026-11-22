package coursehub.async.model;

import coursehub.async.model.Course;

public record LoadOutcome(long courseId, Course course, String error) {
    public boolean succeeded() {
        return error == null;
    }
}
