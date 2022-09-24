package calibration.model;

import java.util.Optional;

public enum TimeInterval {
    ONE_MIN ("1 min"),
    TEN_MIN ("10 min"),
    FIFTEEEN_MIN ("15 min"),
    ONE_HOUR ("1 hour"),
    SIX_HOUR ("6 hour"),
    ONE_DAY ("1 day"),
    ONE_MONTH ("1 month");

    private String string;

    private TimeInterval(String string) {
        this.string = string;
    }

    public String toString() {
        return string;
    }

    public Optional<Integer> minutes() {
        if (this == ONE_MIN) {
            return Optional.of(1);
        } else if (this == TEN_MIN) {
            return Optional.of(10);
        } else if (this == FIFTEEEN_MIN) {
            return Optional.of(15);
        } else if (this == ONE_HOUR) {
            return Optional.of(60);
        } else if (this == SIX_HOUR) {
            return Optional.of(360);
        } else if (this == ONE_DAY) {
            return Optional.of(1440);
        } else {
            return Optional.empty();
        }
    }
}

