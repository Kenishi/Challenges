package favorite_sequence;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Solution3 {

	public static void main(String[] args) throws IOException {
		new Solution3().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int samples = Integer.valueOf(line);
		
		String[] params;
		Integer[] m;
		Rules rules = new Rules();
		for(int sample=0; sample < samples; sample++) {
			// Read the line into int array for easy rule creation 
			int size = Integer.valueOf(reader.nextLine());
			m = new Integer[size];
			params = reader.nextLine().split(" ");
			for(int i=0; i < size; i++) {
				m[i] = Integer.valueOf(params[i]);
			}
			
			createRules(rules, m);
		}
		
		String out = buildSequence(rules) + "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
	
	private void createRules(Rules rules, Integer[] m) {
		for(int cur=0; cur < m.length; cur++) {
			for(int i=0; i < m.length; i++) {
				if(i == cur) { continue; }
				else if(i < cur) { // Current digit, m[cur], comes after m[i]
					rules.valComesAfter(m[cur], m[i]);
				}
				else { // Current digit, m[cur], comes before m[i]
					rules.valComesBefore(m[cur], m[i]);
				}
			}
		}
	}
	
	private String buildSequence(Rules rules) {
		ArrayList<Integer> seq = new ArrayList<>();
		
		for(Integer piv : rules) {
			// First add pivot digit if not present
			if(!seq.contains(piv)) {
				addLexo(seq, rules, piv, 0, seq.size());
			}
			
			// Add numbers before pivot
			for(Integer b : rules.get(piv).before()) {
				if(!seq.contains(b)) { // add before, not present
					addLexo(seq, rules, b, 0, seq.indexOf(piv));
				}
			}
			
			// Add numbers after pivot 
			for(Integer a : rules.get(piv).after()) {
				if(!seq.contains(a)) {
					addLexo(seq, rules, a, seq.indexOf(piv)+1, seq.size());
				}
			}
			
			// Move piv after 'before' numbers
			for(Integer b : rules.get(piv).before()) {
				if(! isBefore(seq, b, piv)) {
					seq.remove(b); // changed from remove(piv) to remove(b), moving b
					addLexo(seq, rules, b, seq.indexOf(b)+1, seq.size());
				}
			}
			
			// Move any numbers that should be after piv, after it
			for(Integer a: rules.get(piv).after()) {
				if(! isAfter(seq, a, piv)) {
					seq.remove(a);
					addLexo(seq, rules, a, seq.indexOf(piv)+1, seq.size());
				}
			}
			
			System.out.println(piv + ": " + seq.toString());
		}
		
		String out = "";
		for(Integer i : seq) {
			out += i + " ";
		}
		return out;
	}
	
	// A is before B in the Seq?
	private boolean isBefore(ArrayList<Integer> seq, Integer a, Integer b) {
		return seq.indexOf(a) < seq.indexOf(b);
	}
	
	// A is after B in the Seq?
	private boolean isAfter(ArrayList<Integer> seq, Integer a, Integer b) {
		return seq.indexOf(a) > seq.indexOf(b);
	}
	
	/*
	 * (f)rom: search  is inclusive
	 * (t)o: search is exclusive
	 */
	private void addLexo(ArrayList<Integer> seq, Rules rules, Integer val, int f, int t) {
		int from = (f < 0 || f > seq.size()) ? 0 : f;
		if(t < f) {
			throw new IndexOutOfBoundsException();
		}
		int to = (t > seq.size()) ? seq.size() : t;
		
		if(seq.size() == 0) { // First val, just append
			seq.add(val);
			return;
		}
		
		RuleSet rule = rules.get(val);
		for(int i=from; i < to; i++) {
			int here = seq.get(i);
			
			if(rule.isBefore(here)) {
				seq.add(i, val);
				return;
			}
			else if(rule.isAfter(here)) {
				continue;
			}
			
			if(val < here) {
				seq.add(i, val);
				return;
			}
		}
		seq.add(to, val);
	}
	
	class Rules implements Iterable<Integer> {
		TreeMap<Integer, RuleSet> set = new TreeMap<>();
		
		public RuleSet addDigit(Integer val) {
			if(set.containsKey(val)) {
				return set.get(val);
			}
			else {
				RuleSet rules = new RuleSet(val);
				set.put(val,  rules);
				return rules;
			}
		}
		
		public void valComesAfter(Integer val, Integer d) {
			RuleSet rules = addDigit(val);
			rules.addComesAfter(d);
		}
		
		public void valComesBefore(Integer val, Integer d) {
			RuleSet rules = addDigit(val);
			rules.addComesBefore(d);
		}
		
		public RuleSet get(int d) {
			return set.get(d);
		}

		@Override
		public Iterator<Integer> iterator() {
			return set.keySet().iterator();
		}
	}
	
	class RuleSet {
		Integer val;
		TreeSet<Integer> before = new TreeSet<>(); // Digits before val
		TreeSet<Integer> after = new TreeSet<>(); // Digits after val
		
		public RuleSet(int val) {
			this.val = val;
		}
		
		/* "Val comes before D" */
		public void addComesBefore(Integer d) {
			after.add(d);
		}
		
		/* "Val comes after D" */
		public void addComesAfter(Integer d) {
			before.add(d);
		}
		
		/* "Does THIS come after D?" */
		public boolean isAfter(Integer d) {
			return before.contains(d);
		}
		
		/* "Does THIS come before D?" */
		public boolean isBefore(Integer d) {
			return after.contains(d);
		}
		
		public Set<Integer> before() {
			return before;
		}
		
		public Set<Integer> after() {
			return after;
		}
	}
}
