package interperter_side;

import java.io.*;
import java.util.*;

import expression.ExpressionConvertor;

public class SetCommand implements Command {

	@Override
	public int doCommand(ArrayList<String> tokens, int index, ArrayList<Double> returned) {
		ArrayList<String> argumants = new ArrayList<String>();
		index = getArgumant(tokens, index, argumants);
		
		if(argumants.get(0).equals("set"))
		{
			if(ParserHelper.scope.getBindedToVariable(argumants.get(1)).size() == 0)
			{
				ParserHelper.scope.setVariable(argumants.get(1), Double.parseDouble(argumants.get(2)));
			}
			else
			{
				for(String b : ParserHelper.scope.getBindedToVariable(argumants.get(1)))
				{
					ParserHelper.out.println("set "+b+" "+argumants.get(2));
					ParserHelper.out.flush();
					for(String v :ParserHelper.scope.getVriablesThatAreBinded(b))
					{
						ParserHelper.scope.setVariable(v, Double.parseDouble(argumants.get(2)));
					}
				}
			}		
		}
		else if (argumants.get(0).equals("bind"))
		{
			ParserHelper.scope.bindVariable(argumants.get(1), argumants.get(2));
			try {Thread.sleep(1000);} catch (InterruptedException e) {}
		}
		return index;
	}

	@Override
	public int getArgumant(ArrayList<String> tokens, int index, List<String> argumants) {
		if(tokens.get(index).equals("bind"))
		{
			argumants.add("bind");
			argumants.add(tokens.get(index-2));
			index++;
			if(tokens.get(index).startsWith("\""))
			{
				index = Lexer.getLexer().readQuote(tokens, index, argumants, true);
				String bind = argumants.get(argumants.size()-1);

				if(bind.startsWith("\"/"))
					bind = bind.substring(2, bind.length()-1);
				else
					bind = bind.substring(1, bind.length()-1);
				
				argumants.set(argumants.size()-1, bind);
			}
			else
			{
				argumants.add(tokens.get(index));
				index++;
			}
		}
		else
		{
			argumants.add("set");
			argumants.add(tokens.get(index-2));
			
			List<String> number = new ArrayList<String>();
			
			index = Lexer.getLexer().readExpression(tokens, index, number);
			Double D = new Double(ExpressionConvertor.calculate(number.get(0)));
			argumants.add(D.toString());
		}
		
		return index;
	}

}
