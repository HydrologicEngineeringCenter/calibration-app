package calibration.statistics;

import hec.io.TimeSeriesContainer;

public abstract class StatisticComputer {
	
	public StatisticComputer() {}
	
	public abstract Statistic computeStatistic(TimeSeriesContainer simulatedFlow, TimeSeriesContainer observedFlow);	

}
