package anagram_test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Solution {
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		// Read number of tests
		String line = reader.nextLine();
		int count = Integer.valueOf(line);
		
		// Start read
		for(int i=0; i < count; i++) {
			// Split at spaces
			line = reader.nextLine();
			String[] vals = line.split(" ");
			if(vals.length != 2) { continue; }
			// Check
			String out = "";
			out = isAnagram(vals[0], vals[1]) ? "true\n" : "false\n";

			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	public boolean isAnagram(String s1, String s2) {
		if(s1 == null || s2 == null) { return false; }
		if(s1.equals("") && s2.equals("")) { return true; }
		if(s1.length() != s2.length()) { return false; }
		
		// Tree Map will keep the keys sorted so we know that anagram should have
		// same exact ordering
		TreeMap<Character, Integer> s1_map = new TreeMap<Character, Integer>();
		TreeMap<Character, Integer> s2_map = new TreeMap<Character, Integer>();
		
		s1_map = countLetters(s1_map, s1);
		s2_map = countLetters(s2_map, s2);
		
		Set<Character> s1_keys = s1_map.keySet();
		Set<Character> s2_keys = s2_map.keySet();
		
		if(s1_keys.size() != s2_keys.size()) { return false; }
		
		Iterator<Character> iter1 = s1_keys.iterator();
		Iterator<Character> iter2 = s2_keys.iterator();
		
		while(iter1.hasNext() && iter2.hasNext()) {
			Character c1 = iter1.next();
			Character c2 = iter2.next();
			if(c1 != c2) { return false; } // Check keys match
			// Check counts match
			if(s1_map.get(c1) != s2_map.get(c2)) { return false; }
		}
		
		return true;
	}
	
	public TreeMap<Character, Integer> countLetters(TreeMap<Character, Integer> map, final String s) {
		for(int i=0; i < s.length(); i++) {
			Character c = s.charAt(i);
			// Pull entry
			if(map.get(c) == null) { // Create entry
				map.put(c, 1);
			}
			else { // Increase
				int val = map.get(c);
				map.put(c, ++val);
			}
		}
		return map;
	}
	
	public void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
}
