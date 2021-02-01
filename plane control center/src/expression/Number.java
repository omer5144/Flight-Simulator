package expression;

public class Number implements Expression {
	private double val;
	
	public Number(double val) {
		super();
		this.val = val;
	}

	@Override
	public double calculate() {
		// TODO Auto-generated method stub
		return val;
	}

}
