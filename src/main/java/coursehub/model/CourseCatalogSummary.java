package coursehub.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public record CourseCatalogSummary(Map<CourseLevel, LevelSummary> byLevel) {
    public CourseCatalogSummary {
        EnumMap<CourseLevel, LevelSummary> copy = new EnumMap<>(CourseLevel.class);
        copy.putAll(byLevel);
        for (CourseLevel level : CourseLevel.values()) {
            copy.putIfAbsent(level, new LevelSummary(0, 0.0));
        }
        byLevel = Collections.unmodifiableMap(copy);
    }
}
