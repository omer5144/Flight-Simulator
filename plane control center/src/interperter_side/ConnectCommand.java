package interperter_side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

import expression.ExpressionConvertor;

public class ConnectCommand implements Command {

	@Override //to fix
	public int doCommand(ArrayList<String> tokens, int index, ArrayList<Double> returned) {
		ArrayList<String> argumants = new ArrayList<String>();
		index = getArgumant(tokens, index, argumants);
		try {
			ParserHelper.simulatorSever = new Socket(argumants.get(0), (int)ExpressionConvertor.calculate(argumants.get(1)));
			ParserHelper.out = new PrintWriter(ParserHelper.simulatorSever.getOutputStream());
		}
		catch (UnknownHostException e) {}
		catch (IOException e) {}
		
		return index;
	}

	@Override
	public int getArgumant(ArrayList<String> tokens, int index, List<String> argumants) {
		argumants.add(tokens.get(index));
		index++;
		index = Lexer.getLexer().readExpression(tokens, index, argumants);
		
		return index;
	}
}
