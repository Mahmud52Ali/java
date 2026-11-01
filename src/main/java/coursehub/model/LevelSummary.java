package coursehub.model;

public record LevelSummary(long count, double averageDurationHours) {
    public LevelSummary {
        if (count < 0 || averageDurationHours < 0) {
            throw new IllegalArgumentException("Показатели сводки не могут быть отрицательными");
        }
    }
}
