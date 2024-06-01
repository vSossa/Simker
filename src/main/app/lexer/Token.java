package main.app.lexer;

public class Token {
	private int index;
	private TokenType type;
	private String stringValue;

	public Token(int index, TokenType type, String stringValue) {
		this.index = index;
		this.type = type;
		this.stringValue = stringValue;
	}	

	public int getIndex()   { return this.index; }
	public TokenType getType() { return this.type; }
	public String getStringValue()   { return this.stringValue; }
}
