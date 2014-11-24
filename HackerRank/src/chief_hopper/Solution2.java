package chief_hopper;

/*
 * Chief Hopper Solution
 * 
 * Weekly Challenges - Week 12 Contest
 * 
 * Passes all but one test case.
 * 
 */

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class Solution2 {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int size = Integer.valueOf(line);
		
		double val = 0.0;
		double weight = 0.0;
		for(int i=0; i < size && i < 62; i++) {
			weight = 1/Math.pow(2, i+1);
			val += weight * reader.nextInt();
		}
		
		int start = (int)Math.ceil(val);
				
		String out = start + "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
}
