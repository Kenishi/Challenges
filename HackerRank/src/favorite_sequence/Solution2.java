package favorite_sequence;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class Solution2 {

	public static void main(String[] args) throws IOException {
		new Solution2().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int samples = Integer.valueOf(line);
				
		String[] params;
		Rules rules = new Rules();
		for(int sample=0; sample < samples; sample++) {
			int size = Integer.valueOf(reader.nextLine());
			
			params = reader.nextLine().split(" ");
			for(int i=0, j=1; j < size; i++, j++) {
				int key = Integer.valueOf(params[i]);
				int before = Integer.valueOf(params[j]);
				rules.add(new Rule(key, before));
			}
		}
		
		// Order the rules 
		ArrayList<Rule> ordered = rules.ordered();
		ArrayList<Integer> seq = new ArrayList<>();
		
		for(Rule rule : ordered) {
			processAB(seq, rule.a, rule.b);
		}
	
		String out = "";
		for(Integer i : seq) {
			out += i + " ";
		}
		out = out.trim() + "\n";
		
		writer.write(out.getBytes());
		writer.flush();
	}
	
	private void processAB(ArrayList<Integer> seq, int a, int b) {
		boolean hasA = seq.contains(a);
		boolean hasB = seq.contains(b);
		int from;
		
		if(!hasA) {
			addLexo(seq, 0, seq.indexOf(b), a);
		}
		if(!hasB) {
			from = seq.indexOf(a);
			addLexo(seq, from+1, -1, b);
		}
		if(hasA && hasB) {
			int aIndex = seq.indexOf(a);
			int bIndex = seq.indexOf(b);
			if(aIndex > bIndex) { // Move 
				seq.remove(aIndex);
				addLexo(seq, 0, bIndex, a);
			}
		}
	}
	
	// From is inclusive in search
	private void addLexo(ArrayList<Integer> seq, int from, int to, int val) {
		from = (from < 0 || from > seq.size()) ? 0 : from;
		to = (to < 0 || to > seq.size()) ? seq.size() : to;
			
		for(int i=from; i < to; i++) {
			int here = seq.get(i);
			
			seq.add(i, val);
			System.out.println(val + " :" + seq.toString());
			seq.remove(i);
			
			if(val < here) {
				seq.add(i, val);
				return;
			}
		}
		
		// Append on end
		seq.add(to, val);
	}
	
	class Rules {
		HashSet<Rule> rules = new HashSet<>();
		
		public void add(Rule r) {
			rules.add(r);
		}
		
		public ArrayList<Rule> ordered() {
			ArrayList<Rule> out = new ArrayList<Rule>();
			
			int b;
			Stack<Integer> bStack = new Stack<>();
			
			Rule r;
			bStack.push(first().a);
			while(!rules.isEmpty()) {
				if(bStack.isEmpty()) {
					bStack.push(first().a);
				}
				
				b = bStack.pop();				
				r = removeFirstA(b);
				if(r != null) {
					out.add(r);
					bStack.push(b);
					bStack.push(r.b);
				}
			}
			
			return out;
		}
		
		public Rule first() {
			return rules.first();
		}
		
		public Rule popFirst() {
			Rule first = null;
			try {
				first = rules.first();
			} catch(NoSuchElementException e) { return null; }
			
			rules.remove(first);
			return first;
		}
		
		public Rule removeFirstA(int a) {
			Rule out = null;
			Iterator<Rule> keyIter = rules.iterator();
			while(keyIter.hasNext()) {
				out = keyIter.next();
				if(out.a == a) { 
					keyIter.remove();
					break;
				}
				else {
					out = null;
				}
			}
			return out;
		}
		
		public ArrayList<Rule> removeBefore(int a) {
			ArrayList<Rule> list = new ArrayList<Rule>();
			
			Iterator<Rule> keyIter = rules.iterator();
			Rule next = null;
			while(keyIter.hasNext()) {
				next = keyIter.next();
				if(next.a == a) {
					list.add(next);
					keyIter.remove();
				}
				if(next.a > a) { break; }
			}
			
			return list;
		}
	}
	
	class Rule implements Comparable<Rule> {
		public int a, b;
		public Rule(int a, int b) {
			this.a = a;
			this.b = b;
		}
		
		public int compareTo(Rule o) {
			if(this.a > o.a) { return 1; }
			else if(this.a < o.a) { return -1; }
			else {
				if(this.b > o.b) { return 1; }
				else if(this.b < o.b) { return -1; }
			}
			return 0;
		}
		
		public int compareTo2(Rule o) {
			int aComp = Integer.toString(this.a).compareTo(Integer.toString(o.a));
			
			if(aComp == 0) {
				int bComp = Integer.toString(this.b).compareTo(Integer.toString(o.b));
				return bComp;
			}
			return aComp;
		}
		
		public String toString() {
			return a + " " + b;
		}
	}
}
