package interperter_side;

import java.util.*;

import expression.ExpressionConvertor;

public class OpenDataServerCommand implements Command {

	@Override
	public int doCommand(ArrayList<String> tokens, int index, ArrayList<Double> returned) {
		ArrayList<String> argumants = new ArrayList<String>();
		index = getArgumant(tokens, index, argumants);
		
		ParserHelper.myServer.start(Integer.parseInt(argumants.get(0)), Integer.parseInt(argumants.get(1)));
		
		return index;
	}

	@Override
	public int getArgumant(ArrayList<String> tokens, int index, List<String> argumants) {
		List<String> number1 = new ArrayList<String>();
		index = Lexer.getLexer().readExpression(tokens, index, number1);
		Double D1 = new Double(ExpressionConvertor.calculate(number1.get(0)));
		argumants.add(D1.toString().substring(0, D1.toString().indexOf(".")));
		
		List<String> number2 = new ArrayList<String>();
		index = Lexer.getLexer().readExpression(tokens, index, number2);
		Double D2 = new Double(ExpressionConvertor.calculate(number2.get(0)));
		argumants.add(D2.toString().substring(0, D2.toString().indexOf(".")));
		
		return index;
	}

}
