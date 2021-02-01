package expression;

public class RegularVar extends Var {
	double val;
	
	public RegularVar(String name, double val) {
		super(name);
		this.val = val;
	}

	@Override
	public double calculate() {
		// TODO Auto-generated method stub
		return val;
	}

	@Override
	public void set(double val) {
		// TODO Auto-generated method stub
		this.val = val;
	}

}
