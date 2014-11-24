package closet_numbers;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

/*
 * 
 * Closet Numbers Solution
 * 
 * Passes all tests
 * 
 * Completes in roughly O(n^2) time
 * 
 */

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int size = Integer.valueOf(line);
		
		String[] params = reader.nextLine().split(" ");
		
		// String -> Int processing + Sorting
		TreeSet<Integer> set = new TreeSet<>();
		for(int i=0; i < size; i++) {
			set.add(Integer.valueOf(params[i]));
		}
		
		// Find smallest diff
		ArrayList<Integer> minVals = new ArrayList<>();
		int min = Integer.MAX_VALUE;
		int diff;
		Integer prev = null;
		for(int val : set) {
			prev = prev==null ? val : prev;
			if(prev == val) { continue; } // 1st time, go to next number
			
			diff = Math.abs(val - prev);
			if(diff < min) {
				min = diff;
				minVals.clear();
				minVals.add(prev);
				minVals.add(val);
			}
			else if(diff == min) {
				minVals.add(prev);
				minVals.add(val);
			}
			prev = val;
		}
		
		// Output
		String out = "";
		for(Integer val : minVals) {
			out += val + " ";
		}
		out += "\n";
		
		writer.write(out.getBytes());
		writer.flush();
	}
}
