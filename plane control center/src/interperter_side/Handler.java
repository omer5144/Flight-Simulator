package interperter_side;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Handler
{
	private List<String> bind;
	public Handler(String path) {
		bind = new ArrayList<String>();
		
		int index = 0;
		
		Scanner s;
		try {
			s = new Scanner(new File(path));
			while(s.hasNextLine())
			{
				String line = s.nextLine();
				if(line.equals("<chunk>"))
				{
					while(s.hasNextLine())
					{
						String l = s.nextLine();
						if(l.equals("</chunk>"))
						{
							break;
						}
						else if(l.startsWith("<node>") && l.endsWith("</node>"))
						{
							bind.add(l.substring(7, l.length()-7));
						}
					}
				}
			}
		} catch (FileNotFoundException e) {e.printStackTrace();}
	}
	
	public void updateTest(String[] values)
	{
		List<String> simX = ParserHelper.scope.getVriablesThatAreBinded("simX");
		List<String> simY = ParserHelper.scope.getVriablesThatAreBinded("simY");
		List<String> simZ = ParserHelper.scope.getVriablesThatAreBinded("simZ");
		
		
		for(String s : simX)
		{
			ParserHelper.scope.setVariable(s, Double.parseDouble(values[0]));
		}
		
		for(String s : simY)
		{
			ParserHelper.scope.setVariable(s, Double.parseDouble(values[1]));
		}
		
		for(String s : simZ)
		{
			ParserHelper.scope.setVariable(s, Double.parseDouble(values[2]));
		}
	}

	public void update(String[] values)
	{
		List<String> vars;
		for(int i=0;i<values.length;i++)
		{
			vars = ParserHelper.scope.getVriablesThatAreBinded(bind.get(i));
			for(String var : vars)
			{
				//System.out.println("the var "+var+" change to "+values[i]+"because binded to "+bind.get(i));
				ParserHelper.scope.setVariable(var, Double.parseDouble(values[i]));
			}
		}
	}
}
