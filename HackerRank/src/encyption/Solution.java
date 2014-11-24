package encyption;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/*
 * Encryption Solution
 * 
 * Passes all tests.
 * 
 * Efficency is around O(length(string))
 * 
 */

public class Solution {
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		
		int m = (int)Math.floor(Math.sqrt(line.length()));
		int n = (int)Math.ceil(Math.sqrt(line.length()));
		m = (m*n) < line.length() ? m+1 : m; // Make sure we fit the word!
		
		int row, col;
		col = m >= n ? m : n;
		row = m < n ? m : n;
				
		// Encrypt
		String out = "";
		int i;
		for(int c=0; c < col; c++) {
			for (int r=0; r < row; r++) {
				i = lookup(line, r, c, row, col);
				if(i < 0) break;
				out += line.charAt(i);
			}
			out += " ";
		}
		out += "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
	
	private int lookup(String word, int r, int c, int rSize, int cSize) {
		int index = (c + (r*cSize));
		return (index >= word.length()) ? -1 : index;
	}
}
