package server_side;
import java.util.*;

public class BFS<T> extends CommonSearcher<T>{
	@Override
	public PathSolution<T> search(Searchable<T> s)
	{
		addToOpenList(s.getInitialState());
		HashSet<State<T>> closedSet=new HashSet<State<T>>();
		while(!openList.isEmpty())
		{
			State<T> n = popOpenList();// dequeue
			//System.out.println("I: "+n+" Papa: " + n.GetCameFrom());
			closedSet.add(n);
			
			if(n.equals(s.getGoalState()))
			{
				return new PathSolution<T>(backTrace(n, s.getInitialState()));
			}
			
			// private method, back traces through the parents
			ArrayList<State<T>> successors = s.getAllPossibleStates(n); //however it is implemented
			
			
			for(State<T> state : successors)
			{
				//System.out.println("succsesors"+state);
				if(!closedSet.contains(state) && !openListContains(state))
				{
					state.SetCameFrom(n);
					state.SetCost(s.Cost(state)+n.GetCost());
					addToOpenList(state);
				}
				else
				{
					if(state.GetCost()>s.Cost(state)+n.GetCost())
					{
						if(!openListContains(state))
						{
							state.SetCost(s.Cost(state)+n.GetCost());
							state.SetCameFrom(n);
							addToOpenList(state);
						}
						else
						{
							openList.remove(state);
							state.SetCost(s.Cost(state)+n.GetCost());
							state.SetCameFrom(n);
							addToOpenList(state);
						}
					
					}

				}
			}
		}
		return null;
	}
		
		private Vector<State<T>> backTrace(State<T> goal, State<T> start)
		{
			Vector<State<T>> v=new Vector<State<T>>();
		
			State<T> n = goal;
			//System.out.println(n.GetCameFrom());
			while(!n.equals(start) && n.IsExistCameFrom())
			{
				v.add(n);
				//System.out.println("Path: "+ n);
				n=n.GetCameFrom();
			}
			v.add(n);

			openList.clear();
			return v;
		}
}
