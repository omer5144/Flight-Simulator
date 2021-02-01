package interperter_side;

import java.util.*;

import expression.ExpressionConvertor;

public abstract class ConditionCommand implements Command {
	public ConditionCommand() {
		super();
	}
	
	public int getArgumant(ArrayList<String> tokens, int index, List<String> argumants)
	{
		index = Lexer.getLexer().readCondition(tokens, index, argumants);
		index = Lexer.getLexer().readBlock(tokens, index, argumants);
		
		return index;
	}
	
	public boolean isTrue(String condition)
	{
		String[] strs = condition.split(",");
		
		switch(strs[1]){
		case "<":
			if(ExpressionConvertor.calculate(strs[0]) < ExpressionConvertor.calculate(strs[2]))
			{
				return true;
			}
			break;
		case ">":
			if(ExpressionConvertor.calculate(strs[0]) > ExpressionConvertor.calculate(strs[2]))
			{
				return true;
			}
			break;
		case "==":
			if(ExpressionConvertor.calculate(strs[0]) == ExpressionConvertor.calculate(strs[2]))
			{
				return true;
			}
			break;
		case "!=":
			if(ExpressionConvertor.calculate(strs[0]) != ExpressionConvertor.calculate(strs[2]))
			{
				return true;
			}
			break;
		case "<=":
			if(ExpressionConvertor.calculate(strs[0]) <= ExpressionConvertor.calculate(strs[2]))
			{
				return true;
			}
			break;
		case ">=":
			if(ExpressionConvertor.calculate(strs[0]) >= ExpressionConvertor.calculate(strs[2]))
			{
				return true;
			}
			break;
		}
		return false;
	}

	public void createScope()
	{
		ParserHelper.scope.createScope();
	}
	
	public void deleteScope() {
		ParserHelper.scope.deleteScope();
	}
}
