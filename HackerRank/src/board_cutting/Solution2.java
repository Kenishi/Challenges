package board_cutting;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;

/*
 * Board Cutting - Editorial Code implement
 * 
 * This is an implementation of the editorial's
 * solution which helped me see the bug in my original
 * code. This code solution is smaller and more succinct
 * than my solution.
 */

public class Solution2 {
	public boolean DEBUG = true; 
	public ArrayList<Long> cutOrder = new ArrayList<>();

	class Cost implements Comparable<Cost>{
		long val = 0;
		boolean isY = false;
		public Cost(long cost, boolean isY) {
			this.val = cost; this.isY = isY;
		}
		@Override
		public int compareTo(Cost o) {
			if(this.val > o.val) { return 1; }
			else if(this.val < o.val) { return -1; }
			else { return 0; }
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int cases = Integer.valueOf(line);
		
		String[] params;
		for(int i=0; i < cases; i++) {
			line = reader.nextLine();
			// Size
			params = line.split(" ");
			int y = Integer.valueOf(params[0]);
			int x = Integer.valueOf(params[1]);
			
			// Costs
			ArrayList<Cost> cuts = new ArrayList<>();
			
			line = reader.nextLine();
			params = line.split(" ");
			ArrayList<Integer> temp = new ArrayList<>();
			for(int j=0; j < y-1; j++) {
				cuts.add(new Cost(Long.valueOf(params[j]), true));
			}
						
			line = reader.nextLine();
			params = line.split(" ");
			for(int j=0; j < x-1; j++) {
				cuts.add(new Cost(Long.valueOf(params[j]), false));
			}
			
			// Sort, create stack
			cuts.sort(Collections.reverseOrder());
			
			long minCost = getMin(cuts);
			String out = minCost + "\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	private long getMin(ArrayList<Cost> cuts) {
		long min = 0;
		
		// Number of segments existing
		int xSegs = 1;
		int ySegs = 1;
		
		for(int i=0; i < cuts.size(); i++) {
			Cost cost = cuts.get(i);
			if(cost.isY) {
				min = cut(min, cost.val, xSegs);
				ySegs++;
			}
			else {
				min = cut(min, cost.val, ySegs);
				xSegs++;
			}
		}		
		return min;
	}
	
	private long cut(long min, long cost, int cutsAcross) {
		return (min + (cutsAcross * cost) % 1000000007) % 1000000007;
	}
}
