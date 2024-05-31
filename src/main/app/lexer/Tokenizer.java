package main.app.lexer;

import java.util.List;
import java.util.ArrayList;

public class Tokenizer {
	public static ArrayList<Token> getTokens(String string) {
		int len = string.length();
		int location, locationEnd;
		ArrayList<Token> tokens = new ArrayList<>();

		location = stripLeft(string, 0);
		if (location < 0) return tokens;

		while (location < len) { 
			Token token;
			char c = string.charAt(location);
			if (c == '"') {
				locationEnd = string.indexOf('"', location + 1);
				if (locationEnd < 0) locationEnd = len;
				token = new Token(location,
								  TokenType.STRING,
								  string.substring(location + 1, locationEnd));
				tokens.add(token);
				location = stripLeft(string, locationEnd + 1);
				if (location == -1) location = len;
			} else {
				if (Character.isDigit(c)) {
					locationEnd = string.indexOf(' ', location + 1);
					if (locationEnd < 0) locationEnd = len;
					token = new Token(location,
									  TokenType.INT,
									  string.substring(location, locationEnd));
					tokens.add(token);
					location = stripLeft(string, locationEnd + 1);
					if (location == -1) location = len;
				} else {
					locationEnd = string.indexOf(' ', location + 1);
					if (locationEnd == -1) locationEnd = len;
					token = new Token(location,
									  TokenType.COMMAND,
									  string.substring(location, locationEnd));
					tokens.add(token);
					location = stripLeft(string, locationEnd + 1);
					if (location == -1) location = len;
				}
			}
		}

		return tokens;
	}

	public static int stripLeft(String string, int StartIndex) {
		int location = StartIndex;
		while (location < string.length() && 
			   Character.isSpaceChar(string.charAt(location))) {
			location++;
		}

		if (location == string.length()) {
			location = -1;
		} 
			
		return location;
	}
}
