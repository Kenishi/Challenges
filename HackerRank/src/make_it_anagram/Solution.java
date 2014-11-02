package make_it_anagram;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class Solution {
	private HashMap<Character, Integer> a_count = new HashMap<>();
	private HashMap<Character, Integer> b_count = new HashMap<>();
	
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), new PrintStream(System.out));
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String a = reader.nextLine();
		String b = reader.nextLine();
		
		// Check Zero cases
		int zeroCase = checkZeroLen(a,b);
		if(zeroCase >= 0) {
			String out = Integer.toString(zeroCase) + "\n";
			writer.write(out.getBytes());
			writer.flush();
			return;
		}
		
		// Tally letters
		count(a, a_count);
		count(b, b_count);
		
		int delCount = 0;
		// Reduce till equal size
		delCount = reduce(a_count, b_count);
			
		// Reduce the other direction
		delCount += reduce(b_count, a_count);
		
		System.out.println(delCount);
	}
	
	private int checkZeroLen(String a, String b) {
		if(a.length() <= 0) {
			return b.length() > 0 ? b.length() : 0;
		}
		else if(b.length() <= 0) {
			return a.length() > 0 ? a.length() : 0;
		}
		return -1;
	}
	
	private void count(String str, HashMap<Character, Integer> map) {
		Character key = null;
		for(int i=0; i < str.length(); i++) {
			key = str.charAt(i);
			int count = map.containsKey(key) ? map.get(key) : 0;
			map.put(key, ++count);
		}
	}
	
	private int reduce(HashMap<Character, Integer> red, HashMap<Character,Integer> ref) {
		int delCount = 0;
		
		Set<Character> keys = red.keySet();
		
		// Find key where red[key].count > ref[key].count
		for(Character key : keys) {
			if(ref.containsKey(key)) {
				int diff = red.get(key) - ref.get(key);
				int val = (diff > 0) ? (red.get(key)-diff) : 0;
				if(val > 0) { 
					red.put(key, 0);
					delCount += diff;
				}
			}
			else {
				delCount += red.get(key);
				red.put(key, 0);
			}
		}
		
		return delCount;
	}
}
