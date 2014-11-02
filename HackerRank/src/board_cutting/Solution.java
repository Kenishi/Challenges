package board_cutting;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;

public class Solution {

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
			line = reader.nextLine();
			params = line.split(" ");
			ArrayList<Integer> temp = new ArrayList<>();
			for(int j=0; j < y-1; j++) {
				temp.add(Integer.valueOf(params[j]));
			}
			
			// Sort, create stack
			Collections.sort(temp);
			Stack<Integer> yCost = new Stack<>();
			yCost.addAll(temp);
			
			
			line = reader.nextLine();
			params = line.split(" ");
			temp.clear();
			for(int j=0; j < x-1; j++) {
				temp.add(Integer.valueOf(params[j]));
			}
			
			// Sort, create stack
			Collections.sort(temp);
			Stack<Integer> xCost = new Stack<>();
			xCost.addAll(temp);
			temp.clear();
			
			long minCost = getMin(xCost, yCost);
			String out = minCost + "\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	/*
	 * Algorithm detail
	 * 
	 * 1) Choose side with current highest cost
	 * 
	 * 1.2) If 2 sides have same highest cost
	 * 		choose the side that will cut across
	 * 		the fewest segments
	 * 1.3) If both cut same was, then pick x
	 * 2) Cut across on that line
	 * 3) Repeat until all lines are cut
	 */
	private long getMin(Stack<Integer> xCosts, Stack<Integer> yCosts) {
		long min = 0;
		
		// Number of segments existing
		int xSegs = 1;
		int ySegs = 1;
		
		int cutsLeft = xCosts.size() + yCosts.size(); // Must use every line
		while(cutsLeft > 0) {
			if(xCosts.isEmpty()) {
				// Do y
				min = cut(min, yCosts.pop(), xSegs);
				++ySegs;
				--cutsLeft;
			}
			else if(yCosts.isEmpty()) {
				// Do x
				min = cut(min, xCosts.pop(), ySegs);
				++xSegs;
				--cutsLeft;
			}
			else if(xCosts.peek() > yCosts.peek()) {
				min = cut(min, xCosts.pop(), ySegs);
				++xSegs;
				--cutsLeft;
			}
			else if(yCosts.peek() > xCosts.peek()) {
				min = cut(min, yCosts.pop(), xSegs);
				++ySegs;
				--cutsLeft;
			}
			else { // Next high is equal in both, take the cut with fewer segs
				if(xSegs < ySegs) { //  Cut on y if x has fewer segs
					min = cut(min, yCosts.pop(), xSegs);
					++ySegs;
					--cutsLeft;
				}
				else if(ySegs <= xSegs) { // Cut on x if y has fewer segs or if segs are equal
					min = cut(min, xCosts.pop(), ySegs);
					++xSegs;
					--cutsLeft;
				}
			}
		}
		
		return min;
	}
	
	private long cut(long min, int cost, int cutsAcross) {
		return (min + (cutsAcross * cost) % 1000000007) % 1000000007;
	}
}
