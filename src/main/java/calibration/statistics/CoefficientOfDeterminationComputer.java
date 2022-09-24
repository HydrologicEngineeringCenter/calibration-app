package calibration.statistics;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import hec.heclib.util.Heclib;
import hec.io.TimeSeriesContainer;

public class CoefficientOfDeterminationComputer extends StatisticComputer {
	
	public CoefficientOfDeterminationComputer() {}
	
	public Statistic computeStatistic (TimeSeriesContainer simulatedFlow, TimeSeriesContainer observedFlow) {
		double value = compute(simulatedFlow.values, observedFlow.values);
		int interval = simulatedFlow.interval;
		String rating = getRating(value, interval);
		return new CoefficientOfDetermination(value, rating);
	}
	
	public static double compute(double[] simulatedFlow, double[] observedFlow) {
		// compute maximum absolute residual
		SummaryStatistics simStats = new SummaryStatistics();
		SummaryStatistics obsStats = new SummaryStatistics();
		for (int i = 1; i < simulatedFlow.length; i++) {
			if (simulatedFlow[i] == Heclib.UNDEFINED_DOUBLE) {
				continue;
			}
			if (observedFlow[i] != Heclib.UNDEFINED_DOUBLE) {
				obsStats.addValue(observedFlow[i]);
				simStats.addValue(simulatedFlow[i]);
			}
		}
		
		if (obsStats.getN() < 1) {
			return Heclib.UNDEFINED_DOUBLE;
		}
		
		double meanObserved = obsStats.getMean();
		double meanSimulated = simStats.getMean();
		SummaryStatistics crossResidual = new SummaryStatistics();
		SummaryStatistics simResidual = new SummaryStatistics();
		SummaryStatistics obsResidual = new SummaryStatistics();
		for (int i = 0; i < simulatedFlow.length; i++) {
			if (simulatedFlow[i] == Heclib.UNDEFINED_DOUBLE) {
				continue;
			}
			if (observedFlow[i] != Heclib.UNDEFINED_DOUBLE) {
				crossResidual.addValue((observedFlow[i] - meanObserved)
						* (simulatedFlow[i] - meanSimulated));
				simResidual.addValue((simulatedFlow[i] - meanSimulated)
						* (simulatedFlow[i] - meanSimulated));
				obsResidual.addValue((observedFlow[i] - meanObserved)
						* (observedFlow[i] - meanObserved));
			}
		}
		
		double r = crossResidual.getSum() /
				(Math.sqrt(simResidual.getSum())
						* Math.sqrt(obsResidual.getSum()));
		double result = r * r;
				
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
