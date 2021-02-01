package server_side;

public class ReverseStringProblem implements Problem
{
	private String str;
	
	public ReverseStringProblem(String string)
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
}
