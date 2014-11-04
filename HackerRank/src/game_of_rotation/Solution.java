package game_of_rotation;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/*
 * Game of Rotation Solution
 * 
 * Passes all tests
 * 
 * I never figured out the trick for this on my own.
 * I knew attempting to do every rotation and check the 
 * weighted sum would cause the processing time to exceed
 * the limits for passing.
 * 
 * I tried looking for best guess solutions by looking for
 * the highest contiguous sum using Kadane's algorithm but
 * that didn't work either.
 * 
 * Once I read the editorial which described the solution,
 * writing the code wasn't hard.
 */

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int size = Integer.valueOf(line);
		
		// Conveyor values
		line = reader.nextLine();
		String[] str = line.split(" ");
		
		long[] values = new long[size];
		long sums = 0; // Sum of all the values
		long wSum = 0; // Initial weighted sum
		
		for(int i=0; i < size; i++) {
			values[i] = Integer.valueOf(str[i]);
			sums += values[i];
			wSum += (i+1)*values[i];
		}
		
		// Get PMEAN
		long pmean = getPmean(values, wSum, sums);
		
		String out = pmean + "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
	
	private long getPmean(long[] values, long prev_wSum, long sums) {
		long max_pmean = prev_wSum;
		long prevwSum = prev_wSum;
		long wSum;
		int size = values.length;
		
		for(int i=1; i < size; i++) {
			wSum = prevwSum - sums + (size * values[lookup(size, i, size-1)]);
			max_pmean = wSum > max_pmean ? wSum : max_pmean;
			prevwSum = wSum;
		}
		
		return max_pmean;
	}
	
	private int lookup(int size, int start, int i) {
		return (start+i) % size;
	}
}

