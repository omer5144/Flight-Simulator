package interperter_side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Command {
	
	public int doCommand(ArrayList<String> tokens, int index, ArrayList<Double> returned);
	
	public int getArgumant(ArrayList<String> tokens, int index, List<String> argumants);
}