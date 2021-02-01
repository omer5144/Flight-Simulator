package server_side;

public class StringReverser implements Solver<ReverseStringSolution, ReverseStringProblem>
{
	public StringReverser()
	{
		
	}
	
 	public ReverseStringSolution Solve(ReverseStringProblem problem)
	{
		return new ReverseStringSolution((new StringBuilder(problem.GetString())).reverse().toString());
	}
}
