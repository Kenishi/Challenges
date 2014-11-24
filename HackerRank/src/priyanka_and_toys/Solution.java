package priyanka_and_toys;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.TreeSet;

/*
 * Priyanka and Toys Solution (Contest Question)
 * 
 * Passed all test cases at Contest time.
 * 
 * Solution uses a Set to only keep the weight once.
 * We don't care if there are 5000 toys of the same weight if we'll get
 * all of them free without buying them.
 * 
 * Worst case time: O(2*N)
 */

public class Solution {

	enum Status {
		SALE,
		BOUGHT;
	}
	
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int count = Integer.valueOf(line);
		
		// Get weights
		TreeSet<Integer> toys = new TreeSet<>();
		for(int i=0; i < count; i++) {
			Integer weight = reader.nextInt();
			toys.add(weight);
		}
		
		// Start buying
		int cost = 0;
		int boughtWeight = -100; // Init to neg so we always buy first item
		for(int buy : toys) {
			if(buy <= boughtWeight+4) { 
				continue;
			}
			else {
				cost++;
				boughtWeight = buy;
			}
		}
		
		// Print Cost
		String out = cost + "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
}
