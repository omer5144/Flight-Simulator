package interperter_side;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DisconnectCommand implements Command {

	@Override
	public int doCommand(ArrayList<String> tokens, int index, ArrayList<Double> returned) {
		try {
			ParserHelper.out.print("bye");
			ParserHelper.out.close();
			ParserHelper.simulatorSever.close();
		} catch (IOException e) {}
		
		return index;
	}

	@Override
	public int getArgumant(ArrayList<String> tokens, int index, List<String> argumants) {
		return 0;
	}

}
