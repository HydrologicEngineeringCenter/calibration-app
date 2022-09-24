package calibration.statistics;

public enum Rating {
	
	VERY_GOOD("Very Good"),
	GOOD("Good"),
	SATISFACTORY("Satisfactory"),
	UNSATISFACTORY("Unsatisfactory");
	
	private String string;
	
	private Rating(String string) {
		this.string = string;
	}

	public String toString() {
		return string;
	}
	
}
