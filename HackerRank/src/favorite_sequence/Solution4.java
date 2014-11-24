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

public class Solution4 {

	public static void main(String[] args) throws IOException {
		new Solution4().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int samples = Integer.valueOf(line);
		
		String[] params;
		Integer[] m;
		RuleArray rules = new RuleArray();
		for(int sample=0; sample < samples; sample++) {
			// Read the line into int array for easy rule creation 
			int size = Integer.valueOf(reader.nextLine());
			m = new Integer[size];
			params = reader.nextLine().split(" ");
			for(int i=0; i < size; i++) {
				m[i] = Integer.valueOf(params[i]);
			}
			
			rules.createRules(m);
		}
		
		String out = buildSequence(rules) + "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
	

	
	private String buildSequence(RuleArray array) {
		ArrayList<Integer> seq = new ArrayList<>();
		
		for(Rules rules : array) { 
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
			}
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
		
		DigitRules rule = rules.get(val);
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
	
	class RuleArray implements Iterable<Rules> {
		ArrayList<Rules> rules = new ArrayList<>();
		
		// Create a new rule for the sample
		private void createRules(Integer[] m) {
			Rules r = new Rules();
			Integer[] beforeVal, afterVal;
			for(int cur=0; cur < m.length; cur++) {
				afterVal = (m.length - cur) <= 0 ? null : new Integer[m.length - cur];
				beforeVal = (cur-1) <= 0 ? null : new Integer[cur-1];
				for(int i=0, j=0; i < m.length; i++, j++) {
					if(i == cur) { 
						j=0;
						continue;
					}
					else if(i < cur) { // Current digit, m[cur], comes after m[i]
						beforeVal[j] = m[i];
					}
					else { // Current digit, m[cur], comes before m[i]
						afterVal[j] = m[i];
					}
				}
				r.valComesAfter(m[cur], beforeVal);
				r.valComesBefore(m[cur], afterVal);
			}
			
			rules.add(r);
			
			// Merge the new sample rules with the existing rules we have
			ArrayList<Rules> n = merge();
			if(n != null) {
				rules = n;
			}
		}
		
		/* Attempt to merge all rules. Return new array of rules, or null if nothing was merged */
		private ArrayList<Rules> merge() {			
			ArrayList<Rules> newList = new ArrayList<>();
			Rules r = null;
			
			for(int cur=0; cur < rules.size(); cur++) {
				r = rules.get(cur);
			
				for(int i=cur+1; i < rules.size(); i++) {
					Rules check = rules.get(i);
					for(Integer key : r) {
						if(check.contains(key)) {
							Rules n = doMerge(r, check);
							newList.add(n);
							break;
						}
					}
				}
			}
			
			return (newList.size() > 0) ? newList : null;
		}
		
		/* Merge A and B into new combined rules */
		private Rules doMerge(Rules a, Rules b) {
			return a.combine(b);
		}
		
		@Override
		public Iterator<Rules> iterator() {
			return rules.iterator();
		}
	}
	
	/*
	 * Rules represent a current "group" of digits that all
	 * hold a relation to each other
	 * 
	 * There may be other Rules which do not overlap because some
	 * numbers are not shared between the different Rules
	 */
	class Rules implements Iterable<Integer> {
		TreeMap<Integer, DigitRules> set = new TreeMap<>();
		ArrayList<Integer> digitOrder = new ArrayList<>();
		
		public DigitRules addDigit(Integer val) {
			if(set.containsKey(val)) {
				return set.get(val);
			}
			else {
				DigitRules rules = new DigitRules(val);
				set.put(val,  rules);
				return rules;
			}
		}
		
		public DigitRules addDigitRules(Integer key, DigitRules d) {
			set.put(key, d);
			return d;
		}
		
		/* Add object to this Rule */
		public Rules combine(Rules o) {
			ArrayList<Integer> oSeq = o.generateSeq();
			ArrayList<Integer> mySeq = this.generateSeq();
			
			for(Integer key : oSeq) {
				if(set.containsKey(key)) {
					get(key).combine(o.get(key));
				}
				else {
					addDigitRules(key, get(key));
				}
				
				
				updateDigitRules()
			}
			
			return this;
		}
		
		public boolean contains(Integer val) {
			return set.containsKey(val);
		}
		
		public void valComesAfter(Integer val, Integer[] d) {
			DigitRules rules = addDigit(val);
			rules.addComesAfter(d);
		}
		
		public void valComesBefore(Integer val, Integer[] d) {
			DigitRules rules = addDigit(val);
			rules.addComesBefore(d);
		}
		
		public DigitRules get(int d) {
			return set.get(d);
		}

		@Override
		public Iterator<Integer> iterator() {
			return set.keySet().iterator();
		}
		
		public String toString() {
			String out = "";
			for(Integer key : set.keySet()) {
				out += key + " ";
			}
			return out;
		}
		
		private ArrayList<Integer> generateSeq() {
			ArrayList<Integer> seq = new ArrayList<>();
			
			for(Integer piv : this) {
				// First add pivot digit if not present
				if(!seq.contains(piv)) {
					addLexo(seq, this, piv, 0, seq.size());
				}
				
				// Add numbers before pivot
				for(Integer b : this.get(piv).before()) {
					if(!seq.contains(b)) { // add before, not present
						addLexo(seq, this, b, 0, seq.indexOf(piv));
					}
				}
				
				// Add numbers after pivot 
				for(Integer a : this.get(piv).after()) {
					if(!seq.contains(a)) {
						addLexo(seq, this, a, seq.indexOf(piv)+1, seq.size());
					}
				}
				
				// Move piv after 'before' numbers
				for(Integer b : this.get(piv).before()) {
					if(! isBefore(seq, b, piv)) {
						seq.remove(b); // changed from remove(piv) to remove(b), moving b
						addLexo(seq, this, b, seq.indexOf(b)+1, seq.size());
					}
				}
				
				// Move any numbers that should be after piv, after it
				for(Integer a: this.get(piv).after()) {
					if(! isAfter(seq, a, piv)) {
						seq.remove(a);
						addLexo(seq, this, a, seq.indexOf(piv)+1, seq.size());
					}
				}
			}
			return seq;
		}
		
		
		private void updateDigitRules(ArrayList<Integer> seq, Integer d) {
			
		}
	}
	
	class DigitRules {
		Integer val;
		TreeSet<Integer> before = new TreeSet<>(); // Digits before val
		TreeSet<Integer> after = new TreeSet<>(); // Digits after val
		
		public DigitRules(int val) {
			this.val = val;
		}
		
		public void combine(DigitRules o) {
			before.addAll(o.before);
			after.addAll(o.after);
		}
		
		/* "Val comes before D" */
		public void addComesBefore(Integer[] d) {
			for(int i=0; i < d.length; i++) {
				after.add(d[i]);
			}
		}
		
		/* "Val comes after D" */
		public void addComesAfter(Integer[] d) {
			for(int i=0; i < d.length; i++) {
				before.add(d[i]);
			}
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
