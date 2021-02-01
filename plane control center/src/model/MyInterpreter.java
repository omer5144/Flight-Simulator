package model;

import interperter_side.Parser;

public class MyInterpreter {

	public static Thread t = null;
	
	public static void close()
	{
		if(t!=null)
			t.stop();
	}
	public static void interpret(String[] lines){
		final String text;
		StringBuilder sb = new StringBuilder("");
		
		for(String line:lines)
		{
			sb.append(line + " ");
		}
		
		text = sb.toString();
		
		close();
		
		t = new Thread(()->Parser.getParser().parser(text));
		
		t.start();

	}
}
