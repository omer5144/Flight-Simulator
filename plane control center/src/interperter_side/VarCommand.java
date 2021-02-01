package interperter_side;

import java.util.ArrayList;
import java.util.List;

public class VarCommand implements Command {

	@Override
	public int doCommand(ArrayList<String> tokens, int index, ArrayList<Double> returned) {
		ArrayList<String> argumants = new ArrayList<String>();
		index = getArgumant(tokens, index, argumants);
		ParserHelper.scope.addVariable(argumants.get(0));
		return index;
	}

	@Override
	public int getArgumant(ArrayList<String> tokens, int index, List<String> argumants) {
		argumants.add(tokens.get(index));
		index++;
		return index;
	}

}
