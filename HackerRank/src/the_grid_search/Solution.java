package the_grid_search;

import java.util.Scanner;
import java.io.IOException;
import java.io.PrintStream;

/*
 * The Grid Search Solution
 * 
 * Passes all tests
 * 
 * Max time is: O(
 * 
 */

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int cases = Integer.valueOf(line);
		
		for(int i=0; i < cases; i++) {
			// Setup grid
			String[] params = reader.nextLine().split(" ");
			int rows = Integer.valueOf(params[0]);
			int cols = Integer.valueOf(params[1]);
			short[][] grid = new short[rows][cols];
			
			for(int r=0; r < rows; r++) {
				line = reader.nextLine();
				for(int c=0; c < cols; c++) {
					grid[r][c] = Short.valueOf(Character.toString(line.charAt(c)));
				}
			}
			
			// Get pattern
			params = reader.nextLine().split(" ");
			int patternRows = Integer.valueOf(params[0]);
			int patternCols = Integer.valueOf(params[1]);
			short[][] pattern = new short[patternRows][patternCols];
			
			for(int r=0; r < patternRows; r++) {
				line = reader.nextLine();
				for(int c=0; c < patternCols; c++) {
					pattern[r][c] = Short.valueOf(Character.toString(line.charAt(c)));
				}
			}
			
			String out = hasPattern(grid, pattern) ? "YES\n" : "NO\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	/* General Search Method */
	private boolean hasPattern(short[][] grid, short[][] pattern) {
		boolean hasPattern = false;
				
		for(int r=0; r < grid.length && !hasPattern; r++) {
			for(int c=0; c < grid[0].length && !hasPattern; c++) {
				if(grid[r][c] == pattern[0][0]) {
					hasPattern = isPattern(grid, pattern, r, c);
				}
			}
		}
		return hasPattern;
	}
	
	/* Checks if possible pattern match is the pattern */
	private boolean isPattern(short[][] grid, short[][] pattern, int row, int col) {
		boolean found = true;
		
		int pR, pC, r, c;
		for(r=row, pR=0; r < grid.length && pR < pattern.length && found; r++, pR++) {
			for(c=col, pC=0; c < grid[0].length && pC < pattern[0].length && found; c++, pC++) {
				found = (grid[r][c] == pattern[pR][pC]);
			}
			// Check that we used all of the pattern's cols
			// MUST check found here or we could overwrite
			// an abort due to invalid value
			found = found && (pC == pattern[0].length);
		}
		// Check that we used all of the pattern's rows
		found = found && (pR == pattern.length);
		
		return found;
	}
}
