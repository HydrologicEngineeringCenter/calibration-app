package calibration.statistics;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import hec.heclib.util.Heclib;
import hec.io.TimeSeriesContainer;

public class NashSutcliffeComputer extends StatisticComputer {
	
	public NashSutcliffeComputer() {}
	
	public Statistic computeStatistic(TimeSeriesContainer simulatedFlow, TimeSeriesContainer observedFlow) {
		double value = compute(simulatedFlow.values, observedFlow.values);
		int interval = simulatedFlow.interval;
		String rating = getRating(value, interval);
		return new NashSutcliffeEfficiency(value, rating);
	}
	
	public double compute(double[] simulatedFlow, double[] observedFlow) {
		// compute maximum absolute residual
		SummaryStatistics stats = new SummaryStatistics();
		for (int i = 0; i < simulatedFlow.length; i++) {
			if (simulatedFlow[i] == Heclib.UNDEFINED_DOUBLE) {
				continue;
			}
			if (observedFlow[i] != Heclib.UNDEFINED_DOUBLE) {
				stats.addValue(observedFlow[i]);
			}
		}
		
		if (stats.getN() < 1) {
			return Heclib.UNDEFINED_DOUBLE;
		}
		
		double meanObserved = stats.getMean();
		double residual;
		SummaryStatistics simResidual = new SummaryStatistics();
		SummaryStatistics obsResidual = new SummaryStatistics();
		for (int i = 0; i < simulatedFlow.length; i++) {
			if (simulatedFlow[i] == Heclib.UNDEFINED_DOUBLE) {
				continue;
			}
			if (observedFlow[i] != Heclib.UNDEFINED_DOUBLE) {
				residual = simulatedFlow[i] - observedFlow[i];
				simResidual.addValue(residual * residual);
				residual = observedFlow[i] - meanObserved;
				obsResidual.addValue(residual * residual);
			}
		}
		
		double result;
		result = 1.0 - (simResidual.getSum() / obsResidual.getSum());
				
		return result;
	}
	
	protected String getRating(double value, int interval) {
		int MINUTES_PER_MONTH = 38*3600;
		
		if (interval < MINUTES_PER_MONTH) {
			if (value > 0.65 && value <= 1.0) {
				return Rating.VERY_GOOD.toString();
			} else if (value > 0.55 && value <= 0.65) {
				return Rating.GOOD.toString();
			} else if (value > 0.40 && value <= 0.55) {
				return Rating.SATISFACTORY.toString();
			} else {
				return Rating.UNSATISFACTORY.toString();
			}
		} else {
			if (value > 0.75 && value <= 1.0) {
				return Rating.VERY_GOOD.toString();
			} else if (value > 0.65 && value <= 0.75) {
				return Rating.GOOD.toString();
			} else if (value > 0.50 && value <= 0.65) {
				return Rating.SATISFACTORY.toString();
			} else {
				return Rating.UNSATISFACTORY.toString();
			}
		}
	}
}
