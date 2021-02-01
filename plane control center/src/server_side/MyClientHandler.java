package server_side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class MyClientHandler implements ClientHandler{

	private Solver<PathSolution<MatrixPath>, Searchable<MatrixPath>> solver;
	private CacheManager<PathSolution<MatrixPath>, MatrixPathProblem> cm;
	
	public MyClientHandler() {
		solver = new SearcherSolver<MatrixPath>(new BFS<MatrixPath>());
		cm = new FileCacheManager<PathSolution<MatrixPath>, MatrixPathProblem>();
	}
	@Override
	public void handleClient(InputStream in, OutputStream out) {
		BufferedReader clientInput = new BufferedReader(new InputStreamReader(in));
		PrintWriter outToClient =new PrintWriter(out);
		
		String line;
		ArrayList<String[]> arrs = new ArrayList<String[]>();
		try {
			while(!(line = clientInput.readLine()).equals("end"))
			{
				arrs.add(line.split(","));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int[][] mat= new int[arrs.size()][arrs.get(0).length];
		for(int i = 0 ; i<arrs.size();i++)
		{
			for(int j = 0;j<arrs.get(0).length;j++)
			{
				mat[i][j] = Integer.parseInt(arrs.get(i)[j]);
			}
		}
		
		String[] arr;
		try {
			line = clientInput.readLine();
			arr = line.split(",");
			int is = Integer.parseInt(arr[0]);
			int js = Integer.parseInt(arr[1]);

			line = clientInput.readLine();
			arr = line.split(",");
			int ie = Integer.parseInt(arr[0]);
			int je = Integer.parseInt(arr[1]);
			
			
			Searchable<MatrixPath> pro = new MatrixPathProblem(is, js, ie, je, mat);
			
			StringBuilder sb = new StringBuilder();
			String answer = "";
			
			if(cm.IsExistSolution((MatrixPathProblem)pro))
			{
				sb.append(cm.GetSolution((MatrixPathProblem)pro));
			}
			else
			{
				PathSolution<MatrixPath> sol = solver.Solve(pro);
			
				Vector<State<MatrixPath>> vec = sol.GetVector();
				Collections.reverse(vec);
				
				State<MatrixPath> first = vec.get(0);
				boolean flag = true;
				for(State<MatrixPath> state:vec)
				{
					if(flag)
					{
						flag = false;
						continue;
					}
					
					if(first.GetState().GetI() < state.GetState().GetI())
					{
						sb.append("Down,");
					}
					else if(first.GetState().GetI() > state.GetState().GetI())
					{
						sb.append("Up,");
					}
					else if(first.GetState().GetJ() < state.GetState().GetJ())
					{
						sb.append("Right,");
					}
					else if(first.GetState().GetJ() > state.GetState().GetJ())
					{
						sb.append("Left,");
					}
					
					first = state;
				}
				
				answer = sb.toString();
				answer = answer.substring(0, answer.length()-1);
				sol.setDetails(answer);
				cm.Save(sol, (MatrixPathProblem)pro);
			}
			
			outToClient.println(answer);
			outToClient.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
