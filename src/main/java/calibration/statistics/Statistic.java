package calibration.statistics;

import java.math.BigDecimal;
import java.math.MathContext;

public abstract class Statistic {

	private double value;
	private String rating;
	
	Statistic(double value, String rating) {
		this.value = value;
		this.rating = rating;
	}

	public abstract String getName();
	
	public double getValue () {
		return (new BigDecimal(value).round(new MathContext(5))).doubleValue();
	}
	
	public String getRating() {
		return rating;
	};
	
}
