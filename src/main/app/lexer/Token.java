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
}
