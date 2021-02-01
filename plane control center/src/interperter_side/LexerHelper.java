package interperter_side;

import java.util.*;

public class LexerHelper {
	public LexerHelper() {
		
	}

	
	public ArrayList<String> lexer(String text)
	{	
		Scanner s = new Scanner(text);
		
		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<String> tokens = new ArrayList<String>();
	
		while(s.hasNext())
			temp.add(s.next());
		s.close();

		for(String str : temp)
		{
			String[] strs = str.split("(?<=[=])|(?=[=])");
			for(String word : strs)
			{
				tokens.add(word);
			}
		}
		return tokens;
	}
	
	public int readQuote(ArrayList<String> tokens, int index, List<String> list, boolean spaces)
	{
		String result = "";
		
		while(true)
		{
			if(index>=tokens.size())
			{
				list.add(result);
				return index+1;	
			}
			String token = tokens.get(index);
			if(spaces && !result.equals(""))
				result = result + " ";
			result = result + token;
			if(token.endsWith("\"") &&!result.equals("\""))
			{
				list.add(result);
				return index+1;					
			}
			
			index++;
		}
	}
	
	//doesn't work when '}' is close to the start of the rest
	public int readBlock(ArrayList<String> tokens, int index, List<String> list)
	{
		String result = "";
		String token;
		int sum = 0;
		boolean flag = true;
		while(flag)
		{
			token = tokens.get(index);
			String[] pieces = token.split("(?<=[{}])|(?=[{}])");
			for(String s:pieces)
			{
				if(s.equals("{"))
				{
					sum++;	
					if(sum !=1)
					{
						result=result+s+" ";
					}
						
				}
				else if(s.equals("}"))
				{
					sum--;
					if(sum==0)
					{
						flag = false;
						break;
					}
					else
					{
						result=result+s+" ";
					}
				}
				else
				{
					result=result+s+" ";
				}
			}
			index++;
		}
		
		if(index<tokens.size() && tokens.get(index).equals("}"))
			index++;
		
		list.add(result);
		return index;
	}
	
	//doesn't work when '{' is close to the end of the expression
	public int readExpression(ArrayList<String> tokens, int index, List<String> list)
	{
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> next = new ArrayList<String>();
		
		while(true)
		{
			next.clear();
			if(index>=tokens.size())
			{
				String end = "";
				for (String s : result)
					end = end+s;
				list.add(end);
				return index;
			}
			String[] strs = tokens.get(index).split("(?<=[-+*/()])|(?=[-+*/()])");
			for(String s : strs)
			{
				Scanner scan = new Scanner(s);
				while(scan.hasNext())
					next.add(scan.next());
				scan.close();
			}
			
			
			if(result.size() == 0)
			{
				for(String token : next)
				{
					if(isNumeric(token) ||isOpertor(token) ||isVar(token) || token.equals("(") || token.equals(")"))
							result.add(token);
				}
			}
			else
			{
				if(matchForExpression(result.get(result.size()-1), next.get(0)))
				{
					result.addAll(next);
				}
				else
				{
					String end = "";
					for (String s : result)
						end = end+s;
					list.add(end);
					return index;
				}
			}
			index++;
			
		}
	}
	
	public int readCondition(ArrayList<String> tokens, int index, List<String> list)
	{
		String result = "";
		boolean flag = true;
		
		
		while(flag)
		{
			if(index>tokens.size())
			{
				String[] strs = result.split("(?<=[<>=!])|(?=[<>=!])");
				if(strs.length == 3)
					result = strs[0] + "," + strs[1] + "," +strs[2];
				if(strs.length == 4)
					result = strs[0] + "," + strs[1] + strs[2] + "," +strs[3];
				
				list.add(result);
				
				return index;
			}
			String token = tokens.get(index);
			if(token.contains("{"))
			{
				flag = false;
				if(!token.equals("{"))
				{
					token = token.substring(0, token.indexOf("{"));
				}
				else
					token = "";
				index--;
			}
			result = result+ token;
			
			index++;
		}
		String[] strs = result.split("(?<=[<>=!])|(?=[<>=!])");
		if(strs.length == 3)
			result = strs[0] + "," + strs[1] + "," +strs[2];
		if(strs.length == 4)
			result = strs[0] + "," + strs[1] + strs[2] + "," +strs[3];
		
		list.add(result);
		
		return index;
	}
	
	private boolean matchForExpression(String str1, String str2)
	{
		
		if(isNumeric(str1) || isVar(str1))
		{
			if(isOpertor(str2))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(str1.equals(")"))
		{
			if(!isOpertor(str2))
				return  false;
			else
				return true;
		}
		else
		{
			return true;
		}
	}
	
 	private boolean isNumeric(String strNum)
	{
		if (strNum == null)
			return false;
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	private boolean isOpertor(String strOp)
	{
		if (strOp == null)
			return false;
		if(strOp.equals("*") || strOp.equals("/") ||strOp.equals("+") || strOp.equals("-"))
			return true;
		return false;
	}

	private boolean isVar(String strVar)
	{
		if (strVar == null)
			return false;
		if(ParserHelper.scope.isExistVar(strVar))
			return true;
		return false;
	}

	private int numOfCharacter(String str, char ch)
	{
		int sum = 0;
		for(char c:str.toCharArray())
		{
			if(c == ch)
				sum++;
		}
		
		return sum;
	}
}
