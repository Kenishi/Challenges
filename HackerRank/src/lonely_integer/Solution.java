package lonely_integer;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/*
 * Lonely Integer Solution
 * 
 * Passes all tests
 * 
 * I ended up using a HashMap instead of the array that I think
 * they wanted, incorrect? This is an O(N^2) solution.
 * 
 * The O(N) solution is done this way:
 * int nonpair(int[] a) {
 * 	int val = 0;
 * 	for(int i=0; i < a.length; i++) { val = val ^ a[i]; }
 * 	return val;
 * }
 * 
 */
public class Solution {
	
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int size = Integer.valueOf(line);
		
		line = reader.nextLine();
		String[] params = line.split(" ");
		
		HashMap<Integer, Boolean> pair = new HashMap<>();
		for(int i=0; i < size ; i++) {
			int val = Integer.valueOf(params[i]);
			
			// For the first occurence we create, on the second we set true
			Boolean hasPair = pair.get(val) == null ? Boolean.FALSE : Boolean.TRUE;
			pair.put(val, hasPair);
		}
		
		Set<Integer> keys = pair.keySet();
		Integer tar = null;
		for(Integer key : keys) {
			if(!pair.get(key)) {
				tar = key;
				break;
			}
		}
		
		String out = tar.toString() + "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
}
