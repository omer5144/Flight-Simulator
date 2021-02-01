package expression;

public class Plus extends BinaryExpression {
	
	public Plus(Expression left, Expression right) {
		super(left, right);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculate() {
		// TODO Auto-generated method stub
		return left.calculate() + right.calculate();
	}
	
}
