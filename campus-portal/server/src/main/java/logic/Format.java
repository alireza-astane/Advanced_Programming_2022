package logic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

final class Format {
    private Format() {
    }

    static String time(LocalDateTime time) {
        return time == null ? "" : time.truncatedTo(ChronoUnit.MINUTES).toString();
    }

    static String score(double value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }
}
