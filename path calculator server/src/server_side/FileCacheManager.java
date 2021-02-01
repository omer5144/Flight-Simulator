package server_side;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileCacheManager<Sol, Pro> implements CacheManager<Sol, Pro>
{
	private Map<Integer, String> map;
	private File f;
	
	public FileCacheManager() {
		f = new File("./files/cache.txt");
		map = new HashMap<Integer, String>();
		
		Scanner s;
		try {
			s = new Scanner(f);
			while(s.hasNextInt())
			{
				Integer i= s.nextInt();
				String str= s.next();
					
				map.put(i, str);
			}
			s.close();
		} catch (FileNotFoundException e) {e.printStackTrace();	}
	}
	
	public boolean IsExistSolution(Pro problem)
	{
		return map.containsKey(problem.hashCode());
	}
	
	public String GetSolution(Pro problem)
	{
		return map.get(problem.hashCode());
	}
	
	public void Save(Sol solution, Pro problem)
	{
		map.put(problem.hashCode(), solution.toString());
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream("./files/cache.txt", true));
			pw.println(problem.hashCode()+" "+solution.toString());
			pw.close();
		} catch (FileNotFoundException e) {	e.printStackTrace();}

		
	}
}
