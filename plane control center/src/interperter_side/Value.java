package interperter_side;

public class Value {
	private boolean exist;
	private double value;
	
	public Value() {
		exist = false;
	}
	
	public void setValue(double value)
	{
		this.value = value;
		exist = true;
	}
	
	public double getValue()
	{
		// does not deal with exeptions
		return value;
	}
}
