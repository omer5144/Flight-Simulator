package server_side;

public class ReverseStringSolution implements Solution
{
	private String str;
		
		public ReverseStringSolution(String string)
		{
			str = string;
		}
		
		public String GetString()
		{
			return str;
		}
		
		public void SetString(String string)
		{
			str = string;
		}
		public void print()
		{
			System.out.println(str);
		}
}
