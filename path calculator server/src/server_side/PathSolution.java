package server_side;
import java.util.*;

public class PathSolution<T> implements Solution {
	private Vector<State<T>> v;
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	private String details;
	
	public PathSolution(Vector<State<T>> v)
	{
		this.v=v; 
		this.details = "";
	}
	
	public Vector<State<T>> GetVector()
	{
		return this.v;
	}
	
	public void SetVector(Vector<State<T>> v)
	{
		this.v = v;
	}

	@Override
	public void print() {
		
		
	}
	
	public String toString()
	{
		return details;
	}
	
	
	

}
