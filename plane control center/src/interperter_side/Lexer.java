package interperter_side;

public class Lexer {

	private static final LexerHelper helper = new LexerHelper();
	
	public static LexerHelper getLexer()
	{
		return helper;
	}
}
