package server_side;
import java.util.*;

public interface Searchable<T> {

	public State<T> getInitialState();
	public State<T> getGoalState();
	public ArrayList<State<T>> getAllPossibleStates(State<T> s);
	public int Cost(State<T> s1);
}
