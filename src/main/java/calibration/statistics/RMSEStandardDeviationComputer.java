package calibration.statistics;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import hec.heclib.util.Heclib;
import hec.io.TimeSeriesContainer;

public class RMSEStandardDeviationComputer extends StatisticComputer {
	
	public RMSEStandardDeviationComputer () {}
	
	public Statistic computeStatistic (TimeSeriesContainer simulatedFlow, TimeSeriesContainer observedFlow) {
		double value = compute(simulatedFlow.values, observedFlow.values);
		int interval = simulatedFlow.interval;
		String rating = getRating(value, interval);
		return new RMSEStandardDeviationRatio(value, rating);
	}
	
	public static double compute(double[] simulatedFlow, double[] observedFlow) {
		// compute sum of observations
		SummaryStatistics obs = new SummaryStatistics();
		for (int i = 1; i < simulatedFlow.length; i++) {
			if (simulatedFlow[i] == Heclib.UNDEFINED_DOUBLE) {
				continue;
			}
			if (observedFlow[i] != Heclib.UNDEFINED_DOUBLE) {
				obs.addValue(observedFlow[i]);
			}
		}
		
		if (obs.getN() < 1) {
			return Heclib.UNDEFINED_DOUBLE;
		}
		
		// compute sum of squared residuals, and sum of squared
		// observation departures
		double residual;
		SummaryStatistics stats = new SummaryStatistics();
		SummaryStatistics stdev = new SummaryStatistics();
		
		for (int i = 0; i < simulatedFlow.length; i++) {
			if (simulatedFlow[i] == Heclib.UNDEFINED_DOUBLE) {
				continue;
			}
			if (observedFlow[i] != Heclib.UNDEFINED_DOUBLE) {
				residual = observedFlow[i] - simulatedFlow[i];
				stats.addValue(residual * residual);
				residual = observedFlow[i] - obs.getMean();
				stdev.addValue(residual * residual);
			}
		}

		double result;
		if (stats.getN() > 0) {
			// root mean square error normalized by standard deviation
			// of observations
			result = Math.sqrt(stats.getSum()) / Math.sqrt(stdev.getSum());
		} else {
			result = Heclib.UNDEFINED_DOUBLE;
		}
		
		return result;
	}
	
	protected String getRating(double value, int interval) {
		int MINUTES_PER_MONTH = 38*3600;
		
		if (interval < MINUTES_PER_MONTH) {
			if (value <= 0.60) {
				return Rating.VERY_GOOD.toString();
			} else if (value <= 0.70) {
				return Rating.GOOD.toString();
			} else if (value <= 0.80) {
				return Rating.SATISFACTORY.toString();
			} else {
				return Rating.UNSATISFACTORY.toString();
			}
		} else {
			if (value <= 0.50) {
				return Rating.VERY_GOOD.toString();
			} else if (value <= 0.60) {
				return Rating.GOOD.toString();
			} else if (value <= 0.70) {
				return Rating.SATISFACTORY.toString();
			} else {
				return Rating.UNSATISFACTORY.toString();
			}
		}
	}
}
