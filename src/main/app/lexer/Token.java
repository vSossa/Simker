package main.app.lexer;

public class Token {
	private int location;
	private TokenType type;
	private String value;

	public Token(int location, TokenType type, String value) {
		this.location = location + 1;
		this.type = type;
		this.value = value;
	}	

	public int getLocation()   { return this.location; }
	public TokenType getType() { return this.type; }
	public String getValue()   { return this.value; }
}
