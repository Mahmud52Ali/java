package coursehub.demo;

import java.util.Objects;

public final class Task11Equality {
    private Task11Equality() {
    }

    public static void run() {
        System.out.println("\nЗадание 11. Равенство в иерархии");
        Point point = new Point(2, 3);
        ColoredPoint coloredPoint = new ColoredPoint(2, 3, "red");
        System.out.println("Loose Point -> ColoredPoint: " + point.equals(coloredPoint));
        System.out.println("Loose ColoredPoint -> Point: " + coloredPoint.equals(point));

        StrictPoint strictPoint = new StrictPoint(2, 3);
        StrictColoredPoint strictColoredPoint = new StrictColoredPoint(2, 3, "red");
        System.out.println("Strict class equality both directions: "
                + strictPoint.equals(strictColoredPoint) + ", " + strictColoredPoint.equals(strictPoint));

        PointValue value = new PointValue(2, 3);
        ColoredPointValue composed = new ColoredPointValue(value, "red");
        System.out.println("Composition keeps equality by value and puts color outside Point: "
                + composed.point().equals(value));
        System.out.println("final value types have one equality definition; extension does not weaken symmetry.");
    }

    private static class Point {
        private final int x;
        private final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Point point && x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static final class ColoredPoint extends Point {
        private final String color;

        private ColoredPoint(int x, int y, String color) {
            super(x, y);
            this.color = color;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof ColoredPoint point && super.equals(point) && color.equals(point.color);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), color);
        }
    }

    private static class StrictPoint {
        private final int x;
        private final int y;

        private StrictPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object other) {
            return other != null && getClass() == other.getClass()
                    && x == ((StrictPoint) other).x && y == ((StrictPoint) other).y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static final class StrictColoredPoint extends StrictPoint {
        private final String color;

        private StrictColoredPoint(int x, int y, String color) {
            super(x, y);
            this.color = color;
        }

        @Override
        public boolean equals(Object other) {
            return other != null && getClass() == other.getClass() && super.equals(other)
                    && color.equals(((StrictColoredPoint) other).color);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), color);
        }
    }

    private record PointValue(int x, int y) {
    }

    private record ColoredPointValue(PointValue point, String color) {
    }
}
