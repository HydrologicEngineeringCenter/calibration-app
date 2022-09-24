package calibration.statistics;

public class NashSutcliffeEfficiency extends Statistic {
	
	NashSutcliffeEfficiency(double value, String rating){
		super(value, rating);
	}

	@Override
	public String getName() {
		return "Nash Sutcliffe Efficiency";
	}

}
