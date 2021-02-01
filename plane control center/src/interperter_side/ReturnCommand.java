package interperter_side;

import java.util.ArrayList;
import java.util.List;

import expression.ExpressionConvertor;

public class ReturnCommand implements Command {

	@Override
	public int doCommand(ArrayList<String> tokens, int index, ArrayList<Double> returned) {
		ArrayList<String> argumants = new ArrayList<String>();
		
		index = getArgumant(tokens, index, argumants);
		
		returned.add(Double.parseDouble(argumants.get(0)));
		
		//try {Thread.sleep(1000);} catch (InterruptedException e1) {}
		
		ParserHelper.myServer.stop();
		
		return index;
	}

	@Override
	public int getArgumant(ArrayList<String> tokens, int index, List<String> argumants) {
		List<String> number = new ArrayList<String>();
		index = Lexer.getLexer().readExpression(tokens, index, number);
		Double D = new Double(ExpressionConvertor.calculate(number.get(0)));
		argumants.add(D.toString());
		
		return index;
	}

}
