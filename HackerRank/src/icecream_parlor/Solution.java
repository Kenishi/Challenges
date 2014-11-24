package icecream_parlor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Ice Cream Parlor Solution
 * 
 * Passes all tests
 * 
 * Kind of loose-n-free on memory.
 * 1 TreeMap<Int, ArrayList> and 3 TreeSets
 * 
 */

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int cases = Integer.valueOf(line);
		
		while(cases > 0) {
			int money = Integer.valueOf(reader.nextLine());
			int count = Integer.valueOf(reader.nextLine());
			TreeMap<Integer, ArrayList<Integer>> flavors = new TreeMap<>();
			
			String[] vals = reader.nextLine().split(" ");
			for(int i=0; i < count; i++) {
				int cost = Integer.valueOf(vals[i]);
				ArrayList<Integer> indexs = 
						(flavors.get(cost) == null) ? new ArrayList<Integer>() : flavors.get(cost);
				indexs.add(i+1);
				flavors.put(cost, indexs);
			}
			// 12
			// 1 2 3 4 5 6 7 8
			// Money - Cost = Other flavor cost
			TreeSet<String> outSet = new TreeSet<>();
			TreeSet<Integer> costUsed = new TreeSet<>();
			for(int cost : flavors.keySet()) {
				int key = money - cost;
				if(key <= 0 || flavors.get(key) == null) { continue; }
				if(costUsed.contains(key)) { break; } // No need to look higher, lower already used higher
				costUsed.add(key); // Don't use this cost again
				
				TreeSet<Integer> set = new TreeSet<>();
				set.addAll(flavors.get(cost));
				set.addAll(flavors.get(key));
				Integer[] array = new Integer[set.size()];
				set.toArray(array);
				
				for(int i=0; i < array.length; i++) {
					for(int j=i+1; j < array.length; j++) {
						String out = array[i] + " " + array[j] + "\n";
						outSet.add(out);
					}
				}
			}
			
			// Print outSet
			for(String out : outSet) {
				writer.write(out.getBytes());
				writer.flush();
			}
			
			cases--;
		}
	}
}
