package cut_the_tree;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import org.w3c.dom.Node;

public class Solution {
	private Node root = null;
	
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int vert = Integer.valueOf(line);
		Node[] nodes = new Node[vert];
		
		String[] params = reader.nextLine().split(" ");
		TreeSet<Integer> leafs = new TreeSet<>();
		for(int i=0; i < vert; i++) {
			nodes[i] = new Node(Integer.valueOf(params[i]));
			leafs.add(i);
		}
		
		int a, b;
		
		for(int i=0; i < vert-1; i++) {
			params = reader.nextLine().split(" ");
			a = Integer.valueOf(params[0]);
			b = Integer.valueOf(params[1]);
			// Find a null node to link up with
			nodes[a].left = (nodes[a].left == null) ? nodes[b] : nodes[a].left;
			nodes[a].right = (nodes[a].right == null) ? nodes[b] : nodes[a].right;
			leafs.remove(a);
			
			nodes[b].parent = nodes[a];
		}
		
		// Sum branches up
		for(int i : leafs) {
			Node cur = nodes[i];
			while(cur.parent != null) {
				cur.parent.addSum(cur.sum());
				cur = cur.parent;
			}
		}
	}
	
	class Node {
		public Node parent;
		public Node left = null;
		public Node right = null;
		private int val = 0;
		private int partSum = 0;
		public Node(int val) {
			this.val = val;
		}
		public void addSum(int s) { this.partSum += s; }
		public int sum() { return this.val + this.partSum; }
	}
}
