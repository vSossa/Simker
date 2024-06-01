package main.app.lexer;

import java.util.List;
import java.util.ArrayList;

public class Tokenizer {
	public static ArrayList<Token> getTokens(String string) {
		int index = 0;
		ArrayList<String> rawTokens = new ArrayList<>();
		ArrayList<Token> tokens = new ArrayList<>();

		rawTokens = getRawTokens(string);
		if (rawTokens.isEmpty()) return tokens;

		final int LEN_RAW_TOKENS = rawTokens.size();
		while (index < LEN_RAW_TOKENS) {
			String rawToken = rawTokens.get(index);
			int len = rawToken.length();
			// STRING 
			if (rawToken.startsWith("\"")) { 
				TokenType type = TokenType.STRING;
				if (index == LEN_RAW_TOKENS - 1) {
					if (len == 1 || !rawToken.endsWith("\"")) {
						System.out.printf("%d: ERROR: unclosed string literal%n",
										  index);
						return null;
					} 

					tokens.add( new Token(index, type, rawToken) );
					++index;
				} else {
					// "string"
					if (len > 1 && rawToken.endsWith("\"")) {
						tokens.add( new Token(index, type, rawToken) );
						++index;

					// "string ..."
					} else {
						int endIndex = index + 1;
						String stringValue = rawToken.concat(" ")
													 .concat(rawTokens.get(endIndex));
						while (endIndex < LEN_RAW_TOKENS && 
							   !stringValue.endsWith("\"")) {
							endIndex++;
							if (endIndex == LEN_RAW_TOKENS) {	
								stringValue = stringValue.concat(" ")
														 .concat(rawTokens.get(endIndex - 1));
							} else {
								stringValue = stringValue.concat(" ")
														 .concat(rawTokens.get(endIndex));
							}
						} 

						if (!stringValue.endsWith("\"")) {
							System.out.printf("%d: ERROR: unclosed string literal%n",
											  endIndex);
							return null;
						}

						tokens.add( new Token(endIndex, type, stringValue) );
						index = endIndex + 1;	
					}
				}

			// INT	
			} else if (len > 0 && Character.isDigit(rawToken.charAt(0))) {
				for (char c : rawToken.toCharArray()) {
					if (!Character.isDigit(c)) {
						System.out.printf("%d: ERROR: invalid integer%n",
										  index);
						return null;
					}
				}
					
				tokens.add( new Token(index, TokenType.INT, rawToken) );	
				++index;

			// NEGATIVE INT
			} else if (len > 1 && rawToken.charAt(0) == '-') {	
				for (int i = 0; i < rawToken.length(); ++i) {
					if (i == 0) continue;

					if (!Character.isDigit(rawToken.charAt(i))) {
						System.out.printf("%d: ERROR: invalid integer%n",
										  index);
						return null;
					}
				}

				tokens.add( new Token(index, TokenType.INT, rawToken) );
				++index;

			// WHITESPACE
			} else if (len == 0) {
				++index;

			// ANYTHING ELSE
			} else {
				tokens.add( new Token(index, TokenType.COMMAND, rawToken) );
				++index;
			}
		}

		return tokens;
	}

	private static int stripLeft(String string, String prefix, int StartIndex) {
		int location = StartIndex;
		int offset = prefix.length();
		int len = string.length();
		while (location < len && 
			   string.startsWith(prefix, location)) {
			location += offset;
		}

		if (location >= len) {
			location = -1;
		} 
			
		return location;
	}

	private static ArrayList<String> getRawTokens(String string) {
		int location, locationEnd;
		int len = string.length();
		ArrayList<String> rawTokens = new ArrayList<>();

		location = stripLeft(string, " ", 0);
		if (location == -1) return rawTokens;

		while (location < len) {
			locationEnd = string.indexOf(' ', location);	
			if (locationEnd == -1) locationEnd = len;
			rawTokens.add(string.substring(location, locationEnd));
			location = locationEnd + 1;
		}

		return rawTokens;
	}
}
