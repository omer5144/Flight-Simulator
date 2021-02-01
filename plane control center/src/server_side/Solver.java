package server_side;

public interface Solver<Sol, Pro>{
	public Sol Solve(Pro problem);
}
