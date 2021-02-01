package interperter_side;

import java.io.*;
import java.net.*;
import java.util.*;

public class ParserHelper {
	private HashMap<String, Command> commandMap;
	public static Scope scope;
	public static Socket simulatorSever;
	public static Server myServer;
	public static PrintWriter out;
	
	public ParserHelper() {
		commandMap = new HashMap<String, Command>();
		commandMap.put("print", new PrintCommand());
		commandMap.put("sleep", new SleepCommand());
		commandMap.put("while", new WhileCommand());
		commandMap.put("if", new IfCommand());
		commandMap.put("connect", new ConnectCommand());
		commandMap.put("openDataServer", new OpenDataServerCommand());
		commandMap.put("var", new VarCommand());
		commandMap.put("=", new SetCommand());
		commandMap.put("return", new ReturnCommand());
		commandMap.put("disconnect", new DisconnectCommand());
		
		scope = new Scope();
		simulatorSever = new Socket();
		myServer = new MyServer();
	}
	
	public double parser(String text)
	{
		int index = 0;
		Command c;
		ArrayList<String> tokens = Lexer.getLexer().lexer(text);;
		ArrayList<Double> returned = new ArrayList<Double>();

		while (index < tokens.size())
		{
			c = commandMap.get(tokens.get(index));
			index++;
			
			if(c!=null)
			{
				index = c.doCommand(tokens, index, returned);
				if(returned.size()>0)
				{
					scope = new Scope();
					//myServer.stop();
					return returned.get(0);
				}
			}			
		}
		return 0;
	}
}
