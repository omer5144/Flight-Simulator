package interperter_side;

import java.util.ArrayList;
import java.util.List;

import expression.*;

class PrintCommand implements Command {

	@Override
	public int doCommand(ArrayList<String> tokens, int index, ArrayList<Double> returned) {
		List<String> argumants = new ArrayList<String>();
		index = getArgumant(tokens, index, argumants);
		System.out.println(argumants.get(0));
		return index;
	}

	@Override
	public int getArgumant(ArrayList<String> tokens, int index, List<String> argumants) {
		
		List<String> toPrint = new ArrayList<String>();
		
		//work for one string
		if(tokens.get(index).startsWith("\""))
		{
			index = Lexer.getLexer().readQuote(tokens, index, toPrint, true);
			argumants.add(toPrint.get(0).substring(1, toPrint.get(0).length()-1));
		}
		else
		{
			index = Lexer.getLexer().readExpression(tokens, index, toPrint);
			Double D = new Double(ExpressionConvertor.calculate(toPrint.get(0)));
			
			if(D.doubleValue() == D.intValue())
			{
				Integer I = new Integer(D.intValue());
				argumants.add(I.toString());	
			}
			else
			{
				argumants.add(D.toString());
			}
		}
		
		return index;
	}

}
