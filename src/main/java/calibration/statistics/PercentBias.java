package calibration.statistics;

public class PercentBias extends Statistic {
	
	String name = "Percent Bias";
	
	PercentBias(double value, String rating){
		super(value, rating);
	}

	@Override
	public String getName() {
		return this.name;
	}

}
