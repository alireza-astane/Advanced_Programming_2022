package logic;

import network.Problem;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/** A weekly class meeting written as {@code Sun 09:00-10:30}. */
record ClassTime(String day, LocalTime start, LocalTime end) {
    private static final List<String> DAYS = List.of("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri");

    static ClassTime parse(String text) {
        try {
            String[] parts = text.trim().split("[ -]");
            ClassTime time = new ClassTime(parts[0], LocalTime.parse(parts[1]), LocalTime.parse(parts[2]));
            if (DAYS.contains(time.day) && time.start.isBefore(time.end)) {
                return time;
            }
        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            // reported below
        }
        throw new Problem("Class times must look like Sun 09:00-10:30.");
    }

    boolean overlaps(ClassTime other) {
        return day.equals(other.day) && start.isBefore(other.end) && other.start.isBefore(end);
    }
}
