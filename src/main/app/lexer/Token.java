package main.app.lexer;

public class Token {
	private int index;
	private TokenType type;
	private String value;

	public Token(int index, 
				 TokenType type, 
				 String value) {
		this.index = index;
		this.type = type;
		this.value = value;
	}	

	public int index()      { return this.index; }
	public TokenType type() { return this.type; }
	public String value()   { return this.value; }

	/* 
	 * Specific for string-tokens because of the
	 * way I construct the Tokens
	 */
	public String prettyValue()   { 
		String pretty = this.value.toString();
		pretty = (pretty.startsWith("\"")) ?
			pretty.replace("\"", "") : pretty.replace("\'", "");
		return pretty; 
	}

	/* 
	 * Specific for string-tokens because of the 
	 *  way I construct the Tokens
	 */
	public void correctIndex(int index) {
		this.index = index;
	}
}
