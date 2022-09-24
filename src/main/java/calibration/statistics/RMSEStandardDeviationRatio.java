package calibration.statistics;

public class RMSEStandardDeviationRatio extends Statistic {
	
	RMSEStandardDeviationRatio(double value, String rating){
		super(value, rating);
	}

	@Override
	public String getName() {
		return "RMSE Standard Deviation Ratio";
	}

}
