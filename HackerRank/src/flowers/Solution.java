package flowers;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

/*
 * Flowers Solution
 * 
 * Passes all tests.
 * 
 * Optimal solution is O(N logN)
 */

class Solution{

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}

	public void run(Scanner reader, PrintStream writer) throws IOException {
		int flowers, people;
		flowers = reader.nextInt();
		people = reader.nextInt();
		  
		Integer[] costs = new Integer[flowers];
		People peeps = new People(people);
		
		for(int i=0; i< flowers; i++) {
		   costs[i] = reader.nextInt();
		}
		Arrays.sort(costs, Collections.reverseOrder());
		
		for(int i=0; i < flowers; i++) {
			peeps.buy(costs[i]);
		}
		
		String out = peeps.getTotal() + "\n";
		writer.write(out.getBytes());
		writer.flush(); 
	}
	   
	static class People {
		int[] peeps = null;
		int cur = 0;
		int total = 0;
		int times = 0; // Increases after everyone has bought a flower
		   
		public People(int size) {
			peeps = new int[size];
		}
		   
		public void buy(int cost) {
			peeps[cur] = (times + 1) * cost;
			total += peeps[cur];
			times = (cur+1 >= peeps.length) ? times+1 : times;
			cur = (cur+1 >= peeps.length) ? 0 : cur+1;	
		}
		   
		public int getTotal() {
			return total;
		}
	}
}