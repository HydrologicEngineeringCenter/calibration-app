package calibration.statistics;

public class PercentDiffPeak extends Statistic {
    private String name = "Percent Difference of Peaks";

    PercentDiffPeak(double value, String rating) {
        super(value, rating);
    }
    @Override
    public String getName() {
        return name ;
    }
}
