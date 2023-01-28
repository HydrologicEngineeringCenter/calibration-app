package calibration.statistics;

import hec.io.TimeSeriesContainer;

public class ValueDiffPeakComputer extends StatisticComputer {
    @Override
    public Statistic computeStatistic(TimeSeriesContainer simulatedFlow, TimeSeriesContainer observedFlow) {
        double computedPeak = simulatedFlow.maximumValue();
        double observedPeak = observedFlow.maximumValue();
        double difference = (computedPeak - observedPeak);
        return new ValueDiffPeak(difference,"NA");
    }
}

