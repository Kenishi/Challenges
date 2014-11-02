package bracket_check;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Stack;

public class Solution {
	
	// Interface to tie enums together
	interface Bracket {}
	
	// Open brack enum
	enum OBracket implements Bracket {
		PAREN('('),
		CURLY('{'),
		SQUARE('[');
		
		private Character m_ch = null;
		private OBracket(char ch) {
			m_ch = ch;
		}
		
		public Character getValue() { return m_ch; }
		public static OBracket fromChar(char ch) {
			switch(ch) {
			case '(' : return PAREN;
			case '{' : return CURLY;
			case '[' : return SQUARE;
			default : return null;
			}
		}
	}
	// Close bracket enum
	enum CBracket implements Bracket {
		PAREN(')'),
		CURLY('}'),
		SQUARE(']');
		
		private Character m_ch = null;
		private CBracket(char ch) {
			m_ch = ch;
		}
		
		public Character getValue() { return m_ch; }
		public static CBracket fromChar(char ch) {
			switch(ch) {
			case ')' : return PAREN;
			case '}' : return CURLY;
			case ']' : return SQUARE;
			default : return null;
			}
		}
		/**
		 * Check if the Open matches Close bracket set
		 * 
		 * @param brack an open bracket to test
		 * @return true if matches, false if it doesn't
		 */
		public boolean isSet(OBracket brack) {
			return brack.toString().equals(this.toString());
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int count = Integer.valueOf(line);
		for(int i=0; i < count; i++) {
			line = reader.nextLine();
			String out = checkBrackets(line) ? "true\n" : "false\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	public boolean checkBrackets(String s1) {
		Stack<Bracket> brackets = new Stack<>();
		
		Bracket brack = null;
		for(int i=0 ; i < s1.length(); i++) {
			char ch = s1.charAt(i);
			brack = checkBracket(ch);
			if(brack != null) {
				if(brack instanceof OBracket) {
					brackets.push(brack);
				}
				else if(brack instanceof CBracket) {
					Bracket next = brackets.peek();
					if(next instanceof OBracket) {
						OBracket open = (OBracket) brackets.pop();
						CBracket close = (CBracket) brack;
						if(close.isSet(open)) { continue; }
						// Could return expected type here
						// which would be close's matching type
						else { return false; }
					}
					else if(next instanceof CBracket) {
						// Could return matching bracket for next
						// in order to fix this
						return false;
					}
				}
			}
		}
		// Could pop the stack here returning the
		// matching bracket for every type, in order to fix
		if(brackets.size() > 0) { return false; }
		
		return true;
	}
	
	private Bracket checkBracket(char ch) {
		if(OBracket.fromChar(ch) != null) { return OBracket.fromChar(ch); }
		if(CBracket.fromChar(ch) != null) { return CBracket.fromChar(ch); }
		return null;
	}
}
