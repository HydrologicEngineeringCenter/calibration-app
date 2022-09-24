package calibration.statistics;

public class CoefficientOfDetermination extends Statistic {
	
	CoefficientOfDetermination(double value, String rating){
		super(value, rating);
	}

	@Override
	public String getName() {
		return "Coefficient of Determination";
	}


}
