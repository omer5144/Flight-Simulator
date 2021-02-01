package interperter_side;

public class Parser {

	private static final ParserHelper helper = new ParserHelper();
	
	public static ParserHelper getParser()
	{
		return helper;
	}
}
