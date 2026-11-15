package coursehub.model;

public enum CourseLevel {
    BEGINNER(1, "Начальный"),
    INTERMEDIATE(2, "Средний"),
    ADVANCED(3, "Продвинутый");

    private final int sortOrder;
    private final String title;

    CourseLevel(int sortOrder, String title) {
        this.sortOrder = sortOrder;
        this.title = title;
    }

    public int sortOrder() {
        return sortOrder;
    }

    public String title() {
        return title;
    }
}
