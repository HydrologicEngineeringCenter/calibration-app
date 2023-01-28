package calibration.statistics;

public class ValueDiffPeak extends Statistic {
    String name = "Difference of Peaks";

    ValueDiffPeak(double value, String rating) {
        super(value, rating);
    }

    @Override
    public String getName() {
        return name;
    }
}
