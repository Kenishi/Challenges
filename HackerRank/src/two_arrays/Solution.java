package two_arrays;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeMap;

/*
 * Two Arrays Solution
 * 
 * Passes all tests
 */

public class Solution {

	public static void main(String[] args) throws IOException{
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		// # of test cases
		int cases = Integer.valueOf(line);
		
		for(int i=0; i < cases; i++) {
			// Get array size
			line = reader.nextLine();
			String[] params = line.split(" ");
			int size = Integer.valueOf(params[0]);
			
			// Get K
			int k = Integer.valueOf(params[1]);
			
			// Get arrays
			String a1 = reader.nextLine();
			String a2 = reader.nextLine();
			
			// Process
			String out = "";
			out = caseExists(a1, a2, k, size) ? "YES\n" : "NO\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	public boolean caseExists(String a1, String a2, int k, int size) {
		/*
		 * Algorithm can be sped up by sorting tree map in DESCENDING order
		 * 
		 * This way when we are pairing A with B, we'll start with a key set
		 * in the most optimal order and reduce search time for a valid number.
		 */
		TreeMap<Integer, Integer> map = new TreeMap<>();
		Scanner scan = new Scanner(a1);
		scan.useDelimiter(" ");
		
		// Fill map with a1
		while(scan.hasNextInt()) {
			int val = scan.nextInt();
			if(map.containsKey(val)) {
				int count = map.get(val);
				map.put(val, ++count);
			}
			else {
				map.put(val, 1);
			}
		}
		scan.close();
		
		// Read in a2
		scan = new Scanner(a2);
		scan.useDelimiter(" ");
		int[] vals = new int[size];
		

		for(int i=0; i < size; i++) {
			vals[i] = scan.nextInt();
		}
		scan.close();
		
		/*
		 * Sorting the second array is important!
		 * We need to make sure we start with small values first
		 * so that we can use up large values in array A first.
		 * 
		 * If value B[i] is large, say B[i] == K, then nearly
		 * any value of A will do, but then we run the risk of
		 * using up a large value of A when say, K - B[i] is large
		 */
		Arrays.sort(vals); 
		
		// Get the lookup keys for array A
		for(int i=0; i < size; i++) {
			int kb = k - vals[i]; // The right side of A >= K - B
			
			boolean found = false;
			for(Integer key : map.keySet()) {
				if(key < kb) { continue; } // Not at start yet
				else { // Look for number that satisfies equallity
					if(map.get(key) > 0) {
						int count = map.get(key);
						map.put(key, --count);
						found = true;
						break;
					}
				}
			}
						
			if(!found) { return false; }
		}
				
		return true;
	}
}
