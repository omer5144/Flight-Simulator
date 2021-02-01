package interperter_side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import expression.ExpressionConvertor;

public class IfCommand extends ConditionCommand {

	public IfCommand() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int doCommand(ArrayList<String> tokens, int index, ArrayList<Double> returned) {
		createScope();
		
		List<String> argumants = new ArrayList<String>();
		index = getArgumant(tokens, index, argumants);
		
		if(isTrue(argumants.get(0)))
		{
			Parser.getParser().parser(argumants.get(1));
		}
		
		deleteScope();
		return index;
	}
}
