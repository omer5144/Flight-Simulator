package interperter_side;

import java.util.ArrayList;
import java.util.List;

import expression.ExpressionConvertor;
import expression.Minus;
import expression.Mul;
import expression.Plus;

public class WhileCommand extends ConditionCommand {

	public WhileCommand() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int doCommand(ArrayList<String> tokens, int index, ArrayList<Double> returned) {
		createScope();
		
		List<String> argumants = new ArrayList<String>();
		index = getArgumant(tokens, index, argumants);
		
		while(isTrue(argumants.get(0)))
		{
			Parser.getParser().parser(argumants.get(1));
		}
		
		deleteScope();
		
		return index;
	}
}
