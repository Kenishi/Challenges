package sherlock_and_pairs;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/*
 * Sherlock and Pairs
 * 
 * Passes all tests
 * 
 * Not too hard to figure out.
 * Once again though, I need to remember to watch variable type or
 * face arithmetic overflow.
 */

public class Solution {

	class Value {
		public long count = 0;
		public long fact = 0;
		public void inc() {
			long oldCount = count;
			count++;
			fact = (count == 1) ? 1 : oldCount * count; 
		}
		public long pairs() {
			return fact==1 ? 0 : fact;
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int cases = Integer.valueOf(line);
		
		for(int i=0; i < cases; i++) {
			line = reader.nextLine();
			
			line = reader.nextLine();
			String out = countPairs(line) + "\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	private long countPairs(String strArray) {
		Scanner scan = new Scanner(strArray);
		scan.useDelimiter(" ");
		
		TreeMap<Long, Value> pairsMap = new TreeMap<>();
		
		while(scan.hasNextInt()) {
			long key = scan.nextInt();
			Value val = pairsMap.get(key)==null ? new Value() : pairsMap.get(key);
			val.inc();
			pairsMap.put(key, val);
		}
		scan.close();
		
		// Count pairs
		long pairs = 0;
		Set<Long> keys = pairsMap.keySet();
				
		for(Long key : keys) {
			Value val = pairsMap.get(key);
			pairs += val.pairs();
		}
		
		return pairs;
	}
}
