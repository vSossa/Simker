package main.app.lexer;

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
					int countStringDelimiter = count(rawToken, "\"", 0);
					if (len == 1) {
						System.out.printf("%d: ERROR: unclosed string literal%n",
										  index);
						return null;
					} else if (!rawToken.endsWith("\"") && countStringDelimiter >= 2) {
						System.out.printf("%d: ERROR: invalid start of command%n",
										  index);
						return null;
					} else if (!rawToken.endsWith("\"")) {
						System.out.printf("%d: ERROR: unclosed string literal%n",
										  index);
						return null;
					} else if (rawToken.endsWith("\"") && countStringDelimiter > 2) {
						System.out.printf("%d: ERROR: invalid string literal%n",
										  index);
						return null;
					} 

					tokens.add( new Token(index, type, rawToken) );
					++index;
				} else {
					// "string"
					if (len > 1 && rawToken.endsWith("\"")) {
						if (count(rawToken, "\"", 0) > 2) {
							System.out.printf("%d: ERROR: invalid string literal%n",
										      index);
							return null;
						} 						

						tokens.add( new Token(index, type, rawToken) );
						++index;

					// "string ..."
					} else {
						int countStringDelimiter;
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
						
						countStringDelimiter = count(stringValue, "\"", 0);
						if (!stringValue.endsWith("\"") && countStringDelimiter >= 2) {
							System.out.printf("%d: ERROR: invalid start of command%n",
											  index);
							return null;
						} 

						if (!stringValue.endsWith("\"")) {
							System.out.printf("%d: ERROR: unclosed string literal%n",
                                              endIndex);
							return null;
						} 

						if (stringValue.endsWith("\"") && countStringDelimiter > 2) {
							System.out.printf("%d: ERROR: invalid string literal%n",
                                              endIndex);
							return null;
						}

						tokens.add( new Token(endIndex, type, stringValue) );
						index = endIndex + 1;	
					}
				}

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

	/*
		strips the prefix out of the string from the startIndex.
		
		returns -1 if prefix is not in string
		otherwise, returns the index right next to the strip prefix.
	*/
	public static int stripLeft(String string, String prefix, int startIndex) {
		int location = startIndex;
		int offset = prefix.length();
		int len = string.length();

		if (startIndex >= len || startIndex < 0) return -1;

		while (location < len && 
			   string.startsWith(prefix, location)) {
			location += offset;
		}

		if (location >= len) location = -1; 
			
		return location;
	}

	/*
		count the number of times s2 is in s1 from the startIndex
		until the end.

		if startIndex is out of bounds, returns -1
		if s2 == "", returns s1.length()
		if s2.length() > s1.length(), returns 0
		else returns the count	
	*/
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

	/*
		slices the string by a single whitespace and returns those slices. 
		if the string is empty, returns an empty array.
		
		the only spaces that are strip off the string are those in 
		the beggining. 
	*/
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
