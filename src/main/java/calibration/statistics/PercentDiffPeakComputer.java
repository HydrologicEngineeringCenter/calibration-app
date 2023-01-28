package calibration.statistics;

import hec.io.TimeSeriesContainer;

public class PercentDiffPeakComputer extends StatisticComputer {
    @Override
    public Statistic computeStatistic(TimeSeriesContainer simulatedFlow, TimeSeriesContainer observedFlow) {
        double computedPeak = simulatedFlow.maximumValue();
        double observedPeak = observedFlow.maximumValue();
       double difference = (computedPeak - observedPeak);
       double average = (computedPeak + observedPeak)/2.0;
       double percentDiff = difference/average;
       return new PercentDiffPeak(percentDiff,"NA");
    }
}
