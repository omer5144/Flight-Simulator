package server_side;
import java.util.*;

public abstract class CommonSearcher<T> implements Searcher<T>{

	protected PriorityQueue<State<T>> openList;
	private int evaluatedNodes;
	
	public CommonSearcher()
	{
		openList = new PriorityQueue<State<T>>();
		evaluatedNodes=0;
	}
	
	protected State<T> popOpenList() {
	evaluatedNodes++;
	return openList.poll();
	}
	
	protected void addToOpenList(State<T> s) {
		openList.add(s);
	}
	
	protected boolean openListContains(State<T> s)
	{
		return openList.contains(s);
	}
	
	@Override
	public abstract PathSolution<T> search(Searchable<T> s);
	@Override
	public int getNumberOfNodesEvaluated() {
	return evaluatedNodes;
	}
}
