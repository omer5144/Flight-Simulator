package server_side;

public class SearcherSolver<T> implements Solver<PathSolution<T>, Searchable<T>>
{
	Searcher<T> algo;
	
	public SearcherSolver(Searcher<T> s)
	{
		this.algo = s;
	}
	
	public PathSolution<T> Solve(Searchable<T> problem)
	{
		return algo.search(problem);
	}
}
