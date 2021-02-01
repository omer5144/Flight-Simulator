package server_side;

public class State<T> implements Comparable<State<T>> {

	private T state;
	private int cost; 
	private State<T> cameFrom;
	
	public State(T state){ 
	this.state = state;
	this.cameFrom = null;
	}
	
	@Override
	public boolean equals(Object s)
	{ 
		State<T> news = (State<T>)s;
		return state.equals(news.state);
	}
	
	public T GetState() {
		return this.state;
	}
	public int GetCost() {
		return this.cost;
	}
	public State<T> GetCameFrom() {
		return this.cameFrom;
	}
	public boolean IsExistCameFrom() {
		return this.cameFrom != null;
	}
	public void SetCost(int cost) {
		this.cost=cost;
	}
	public void SetCameFrom(State<T> cameFrom) {
		this.cameFrom=cameFrom;
	}
	public int	compareTo(State<T> s)
	{
		return this.cost-s.cost;
	}
	
	public String toString()
	{
		return state.toString();
	}
	
	public int hashCode()
	{
		return (state.hashCode());
	}
}
