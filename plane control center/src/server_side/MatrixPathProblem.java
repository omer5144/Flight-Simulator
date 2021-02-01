package server_side;

import java.util.ArrayList;
import java.util.Arrays;

public class MatrixPathProblem implements Searchable<MatrixPath>{

	private int[][] mat;
	int is,js,ie,je;
	
	public MatrixPathProblem(int istart, int jstart, int iend, int jend, int[][] mat)
	{
		this.mat = mat;
		is = istart;
		js = jstart;
		ie = iend;
		je = jend;
		
	}
	public State<MatrixPath> getInitialState() {
		State<MatrixPath> init = new State <MatrixPath>(new MatrixPath(is, js));
		init.SetCost(mat[is][js]);
		return init;
	}
	public State<MatrixPath> getGoalState() {
		return new State <MatrixPath>(new MatrixPath(ie, je));
	}
	@Override
	public ArrayList<State<MatrixPath>> getAllPossibleStates(State<MatrixPath> s)
	{
		ArrayList<State<MatrixPath>> a=new ArrayList<State<MatrixPath>>();
		if(s.GetState().GetI() == 0)
		{
			a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI()+1, s.GetState().GetJ()))); // Down
			if(s.GetState().GetJ() == 0)
			{
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()+1))); // Right
			}
			else if(s.GetState().GetJ() == mat[0].length-1)
			{
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()-1))); // Left
			}
			else
			{
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()+1))); // Right
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()-1))); // Left
			}
		}
		else if(s.GetState().GetI() == mat.length-1)
		{
			a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI()-1, s.GetState().GetJ()))); // Up
			if(s.GetState().GetJ() == 0)
			{
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()+1))); // Right
			}
			else if(s.GetState().GetJ() == mat[0].length-1)
			{
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()-1))); // Left
			}
			else
			{
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()+1))); // Right
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()-1))); // Left
			}
		}
		else
		{
			a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI()+1, s.GetState().GetJ()))); // Down
			a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI()-1, s.GetState().GetJ()))); // Up
			if(s.GetState().GetJ() == 0)
			{
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()+1))); // Right
			}
			else if(s.GetState().GetJ() == mat[0].length-1)
			{
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()-1))); // Left
			}
			else
			{
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()+1))); // Right
				a.add(new State<MatrixPath> (new MatrixPath(s.GetState().GetI(), s.GetState().GetJ()-1))); // Left
			}
		}
		return a;
	}
	@Override
	public int Cost(State<MatrixPath> s1) {
		return mat[s1.GetState().GetI()][s1.GetState().GetJ()];
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ie;
		result = prime * result + is;
		result = prime * result + je;
		result = prime * result + js;
		result = prime * result + Arrays.deepHashCode(mat);
		return result;
	}
	
}
