package main.app.lexer;

import java.util.ArrayList;

public class Tokenizer {
	public static ArrayList<Token> getTokens(String string) {
		int index = 0;
		ArrayList<String> rawTokens = new ArrayList<>();
		ArrayList<Token> tokens = new ArrayList<>();

		rawTokens = splitString(string, ' ');
		if (rawTokens.isEmpty()) return null;

		final int LEN_RAW_TOKENS = rawTokens.size();
		while (index < LEN_RAW_TOKENS) {
			String rawToken = rawTokens.get(index);
			int len = rawToken.length();

			// "STRING"
			if (count(rawToken, "\"", 0) >= 1) { 
				int startIndex = index;	
				Token stringToken = buildStringToken(index, "\"", rawTokens);

				if (stringToken.value().length() == 1 ||
  					(!stringToken.value().startsWith("\"") || !stringToken.value().endsWith("\"")) ||
                    (count(stringToken.value(), "\"", 0) > 2)) {
					System.out.printf("%d: ERROR: invalid string%n",
									  startIndex);
					return null;
				} 

				tokens.add(stringToken);
				index = stringToken.index();

			// 'STRING'
			} else if (count(rawToken, "\'", 0) >= 1) {
				int startIndex = index;	
				Token stringToken = buildStringToken(index, "\'", rawTokens);

				if (stringToken.value().length() == 1 ||
  					(!stringToken.value().startsWith("\'") || !stringToken.value().endsWith("\'")) ||
                    (count(stringToken.value(), "\'", 0) > 2)) {
					System.out.printf("%d: ERROR: invalid string%n",
									  startIndex);
					return null;
				} 

				tokens.add(stringToken);
				index = stringToken.index();

			// WHITESPACE
			} else if (len == 0) {
				++index;

			// INT	
			} else if (Character.isDigit(rawToken.charAt(0))) {
				for (char c : rawToken.toCharArray()) {
					if (!Character.isDigit(c)) {
						System.out.printf("%d: ERROR: invalid integer%n",
										  index);
						return null;
					}
				}
					
				tokens.add( new Token(index, TokenType.INT, rawToken) );	
				++index;

			// COMMAND
			} else {
				tokens.add( new Token(index, TokenType.COMMAND, rawToken) );
				++index;
			}
		}

		return tokens;
	}

	public static Token buildStringToken(int indexStart, String delimiter, ArrayList<String> stringParts) {
		String value = stringParts.get(indexStart);	

		++indexStart;
		while (indexStart < stringParts.size() &&
			   count(value, delimiter, 0) < 2) {
			value = value.concat(" ")
						 .concat(stringParts.get(indexStart));
			++indexStart;
		}

		return new Token(indexStart, TokenType.STRING, value);
	}

	public static int stripLeft(String string, String prefix, int startIndex) {
		int location = startIndex;
		int offset = prefix.length();
		int len = string.length();

		if (startIndex >= len || 
			startIndex < 0) {
			return -1;
		} 

		while (location < len && 
			   string.startsWith(prefix, location)) {
			location += offset;
		}

		if (location >= len) location = -1; 
			
		return location;
	}

	public static int count(String s1, String s2, int startIndex) {
		int counter = 0;
		int len = s1.length();
		int offset = s2.length();

		if (startIndex > len || startIndex < 0) return -1;	
		if (offset == 0) return len - startIndex;
		if (offset > len - startIndex) return counter;


		for (int i = startIndex; i < len; i += offset) {
			if (s1.substring(i, i + offset).equals(s2)) counter++;
		}

		return counter;
	}

	public static ArrayList<String> splitString(String string, char delimiter) {
		int location, locationEnd;
		int len = string.length();
		ArrayList<String> rawTokens = new ArrayList<>();

		location = stripLeft(string, " ", 0);
		if (location == -1) return rawTokens;

		while (location < len) {
			locationEnd = string.indexOf(delimiter, location);	
			if (locationEnd == -1) locationEnd = len;
			rawTokens.add(string.substring(location, locationEnd));
			location = locationEnd + 1;
		}

		return rawTokens;
	}
}
