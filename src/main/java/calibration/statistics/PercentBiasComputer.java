package calibration.statistics;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import hec.heclib.util.Heclib;
import hec.io.TimeSeriesContainer;

public class PercentBiasComputer extends StatisticComputer{
	
	public PercentBiasComputer () {};
	
	public Statistic computeStatistic (TimeSeriesContainer simulatedFlow, TimeSeriesContainer observedFlow) {
		double value = compute(simulatedFlow.values, observedFlow.values);
		int interval = simulatedFlow.interval;
		String rating = getRating(value, interval);
		return new PercentBias(value, rating);
	}
	
	public static double compute(double[] simulatedFlow, double[] observedFlow) {
		// compute sum of residuals
		SummaryStatistics residual = new SummaryStatistics();
		SummaryStatistics obs = new SummaryStatistics();

		for (int i = 1; i < simulatedFlow.length; i++) {
			if (observedFlow[i] != Heclib.UNDEFINED_DOUBLE) {
				if (simulatedFlow[i] == Heclib.UNDEFINED_DOUBLE) {
					continue;
				}
				residual.addValue(observedFlow[i] - simulatedFlow[i]);
				obs.addValue(observedFlow[i]);
			}
		}

		double result;
		if (residual.getN() > 0) {
			// sum of residuals normalized by sum of observations
			result = 100.0 * (residual.getSum() / obs.getSum());
		} else {
			result = Double.NaN;
		}
		
		return result;
	}
	
	protected String getRating(double value, int interval) {
		int MINUTES_PER_MONTH = 38*3600;
		
		if (interval < MINUTES_PER_MONTH) {
			if (Math.abs(value) < 15) {
				return Rating.VERY_GOOD.toString();
			} else if (Math.abs(value) < 20) {
				return Rating.GOOD.toString();
			} else if (Math.abs(value) < 30) {
				return Rating.SATISFACTORY.toString();
			} else {
				return Rating.UNSATISFACTORY.toString();
			}
		} else {
			if (Math.abs(value) < 10) {
				return Rating.VERY_GOOD.toString();
			} else if (Math.abs(value) < 15) {
				return Rating.GOOD.toString();
			} else if (Math.abs(value) < 25) {
				return Rating.SATISFACTORY.toString();
			} else {
				return Rating.UNSATISFACTORY.toString();
			}
		}
	}

}
