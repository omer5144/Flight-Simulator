package server_side;

public interface Searcher<T> {

	public PathSolution<T> search(Searchable<T> s);
	
	public int getNumberOfNodesEvaluated();
}
