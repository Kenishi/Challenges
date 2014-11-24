package favorite_sequence;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int samples = Integer.valueOf(line);
		
		// Create index lookup and initialize
		// Lookup array maps a digit to its location in the
		//  sequence list. ex: '3' may be at index 8 in the sequence
		TreeMap<Integer, Node> lookup = new TreeMap<>();
		
		String[] params;
		Node mainHead = null;
		for(int sample=0; sample < samples; sample++) {
			int size = Integer.valueOf(reader.nextLine());
			
			params = reader.nextLine().split(" ");
			int before, after;
			for(int i=0, j=1; j < size; i++, j++) {
				before = Integer.valueOf(params[i]);
				after = Integer.valueOf(params[j]);
				
				if(lookup.get(before) != null) {
					if(lookup.get(after) != null) { // Both exist
						// Check if in same list
						if(getListHead(lookup.get(before)) == getListHead(lookup.get(after))) { // Same list
							if(! isBefore(lookup.get(before), lookup.get(after))) { // Swap
								swap(lookup.get(before), lookup.get(after));
							}
						}
						/* POSSIBLE BUGGY AREA */
						else { // Merge in
							// Pull out the target from after
							Node afterNode = lookup.get(after);
							if(afterNode.prev != null) {
								afterNode.prev.next = null; // Make previous node the end of the list
							}
							
							// afterNode may be the list head, so there may exist no "previous list"
							Node tempPrevList = (afterNode.prev != null) ? afterNode.listHead : null;
							
							Node tempNextList = (afterNode.next != null) ? afterNode.next : null;
							if(tempNextList != null) {
								tempNextList.prev = null;
							}
							
							afterNode.listHead = getListHead(lookup.get(before));
							insertLexo(lookup.get(before), afterNode, null);
							
							// Merge remaining list parts
							if(tempPrevList != null) {
								rangeInsert(tempPrevList, getListHead(afterNode.listHead), afterNode);
							}
							if(tempNextList != null) {
								rangeInsert(tempNextList, afterNode, null);
							}
						}
						
					}
					else { // After doesn't exist yet
						Node afterNode = new Node(after);
						afterNode.listHead = getListHead(lookup.get(before));
						lookup.put(after, afterNode);
						
						insertLexo(lookup.get(before), afterNode, null);
					}
				}
				else { 
					// Before doesn't exist, see if after does
					if(lookup.get(after) != null) {
						Node beforeNode = new Node(before);
						
						beforeNode.listHead = getListHead(lookup.get(after));
						lookup.put(before, beforeNode);
					
						insertLexo(beforeNode.listHead, beforeNode, lookup.get(after));
					}
					else { // Neither exist
						// Create nodes
						Node beforeNode = new Node(before);
						beforeNode.listHead = null; // This is the list head
						lookup.put(before, beforeNode);
						
						Node afterNode = new Node(after);
						afterNode.listHead = beforeNode;
						lookup.put(after, afterNode);
						
						beforeNode.putAfter(afterNode);
					}
				}
				//debug(lookup);
			}
		}
		
		// Check for hanging lists and merge lexographically
		ArrayList<Node> listHeads = new ArrayList<>();
		Node curHead = null;
		for(Integer key : lookup.keySet()) {
			curHead = getListHead(lookup.get(key));
			if(!listHeads.contains(curHead)) {
				listHeads.add(curHead);
			}
		}
		
		if(listHeads.size() > 1) {
			mainHead = listHeads.get(0);
			for(int i=1; i < listHeads.size(); i++) {
				Node list = listHeads.get(i);
				rangeInsert(list,mainHead, null);
			}
		}
		else {
			mainHead = listHeads.get(0);
		}
	
		// Output result
		Node cur = mainHead;
		String out = "";
		while(cur != null) {
			out += cur.val + " ";
			cur = cur.next;
		}
		out = out.trim() + "\n";
		
		writer.write(out.getBytes());
		writer.flush();
	}
	
	/*
	 * Start is not generally inclusive UNLESS it's the head
	 */
	private void insertLexo(Node start, Node insert, Node stop) {
		boolean isInserted = false;
		Node cur = start;
		
		// Check if we need to swap with head
//		if(cur.val > insert.val) {
//			if(cur.listHead == null) { // Confirm cur is listHead
//				swap(start, insert);
//				isInserted = true;
//			}
//		}

		while(!isInserted) {
			if(cur == stop || cur.val > insert.val) {
				cur.putBefore(insert);
				isInserted = true;
			}
			else if(cur.next == null || cur.next.val > insert.val || cur.next == stop) { 
				// The next node's value is greater OR the next node is STOP point, so add after current
				cur.putAfter(insert);
				isInserted = true;
			}
			
			cur = cur.next;
		}
	}

	private void swap(Node a, Node b) {
		
		// Check if nodes are next to each other
		if(b.next != null && b.next.val == a.val) { // Fix ordering?
			Node temp = a;
			a = b;
			b = temp;
		}
		
		if(a.next != null && a.next.val == b.val) { // B follows A and they are side-by-side,  put A after B 
			b.putAfter(a);
		}
		else { // Normal swap, A and B may be some distance apart, put A after B.prev, put B after A.prev
			Node aPrev = a.prev;
			Node aNext = a.next;
			
			Node bPrev = b.prev;
			Node bNext = b.next;
			
			if(aPrev == null) {
				aNext.putBefore(b);
			}
			else {
				aPrev.putAfter(b);
			}
			
			if(bPrev == null) {
				bNext.putBefore(a);
			}
			else {
				bPrev.putAfter(a);
			}
		}
	}
	
	/*
	 * Check that before precedes after
	 */
	private boolean isBefore(Node before, Node after) {
		boolean encountered = false;
		Node cur = before;
		while(cur.next != null && !encountered) {
			encountered = (cur.val == after.val);
			cur = cur.next;
		}
		return encountered;
	}
	
	/*
	 * Helper function to handle inserting a list with a set range
	 */
	private void rangeInsert(Node list, Node start, Node stop) {
		Node cur = list;
		Node insert;
		while(cur != null) {
			insert = cur;
			insert.listHead = getListHead(start); // Set the inserting node's listHead to new list
			
			cur = cur.next; // Move cur to next node before insert, otherwise we won't know what's next in list
			
			insertLexo(start, insert, stop);
			start = insert; // Move start to insert, must maintain rules of the inserting list
		}
	}
	
	/*
	 * To cut down on list modifications when swapping nodes;
	 * Previously added nodes may be pointing to the wrong listHead,
	 * if the list head was swapped at some point.
	 * 
	 * When a head is swapped the old head points to the new head
	 * and the new head points to nothing signaling that it is the
	 * list Head.
	 * 
	 * This method finds the curent list head.
	 */
	private Node getListHead(Node n) {
		Node cur = n;
		
		while(cur.listHead != null) {
			cur = cur.listHead;
		}
		
		return cur;
	}
	
	private void debug(TreeMap<Integer, Node> lookup) {
		Node curHead = null;
		ArrayList<Node> heads = new ArrayList<>();
		for(Integer key : lookup.keySet()) {
			curHead = getListHead(lookup.get(key));
			if(!heads.contains(curHead)) {
				heads.add(curHead);
			}
		}
		
		System.out.println("=======");
		// print
		for(int i=0; i < heads.size(); i++) {
			Node cur = heads.get(i);
			print(cur);
		}
		System.out.println("=======");
	}
	
	private void print(Node n) {
		Node cur = n;
		String out = "";
		while(cur != null) {
			out += cur.val + " ";
			cur = cur.next;
		}
		System.out.println(out);
	}
	
	class Node {
		public Node listHead = null;
		public Node prev = null;
		public Node next = null;
		public int val = -1;
		
		public Node(int v) {
			this.val = v;
		}
		
		// Put n before this. N and THIS should have same listHead
		// 5 6 // 5.putBefore(6)
		public void putBefore(Node n) {
			if(getListHead(this) != getListHead(n)) {
				// Modifying order when lists are different will break state
				throw new RuntimeException("Attempting insert before when listHeads are different.");
			}
			
			// Patch list, remove surounding nodes refs to N
			if(n.prev != null) {
				n.prev.next = n.next;
			}
			if(n.next != null) {
				n.next.prev = n.prev;
			}
			
			// Update listHead, N will become listHead
			if(listHead == null) {
				n.listHead = null;
				n.prev = null;
				this.listHead = n;
			}
						
			// Insert before THIS
			n.prev = this.prev;
			if(n.prev != null) {
				n.prev.next = n;
			}
			n.next = this;
			this.prev = n;
		}
		
		// Put n after this. N and THIS should have same listHead
		public void putAfter(Node n) {
			if(getListHead(this) != getListHead(n)) {
				// Modifying order when lists are different will break state
				throw new RuntimeException("Attempting insert after when listHeads are different.");
			}
						
			// Patch List, remove surrounding nodes refs to N
			if(n.prev != null) {
				n.prev.next = n.next;
			}
			if(n.next != null) {
				n.next.prev = n.prev;
			}
			
			// Update list head if N is listhead
			if(n.listHead == null) {
				n.listHead = n.next;
				n.next.listHead = null;
			}
			
			// Insert after THIS
			n.next = this.next;
			if(n.next != null) {
				n.next.prev = n;
			}
			n.prev = this;
			this.next = n;
		}
		
		public Node getListNode() {
			Node cur = this.listHead;
			
			while(cur.listHead != null) {
				cur = cur.listHead;
			}
			
			return cur;
		}
	}
}
