package interperter_side;

import expression.RegularVar;
import expression.Var;

public class Utilities {
	public static boolean isDouble(String str) {
		try {
			double d = Double.parseDouble(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	public static Var getVar(String name) {
		Scope scope =  ParserHelper.scope;
		Value value = scope.getVariable(name);
		if(value == null)
			return null;
		return new RegularVar(name, value.getValue());
		
		
	}
}
