package mark_and_toys;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

/*
 * Mark and Toys
 * 
 */

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		String[] params = line.split(" ");
		
		int size = Integer.valueOf(params[0]);
		int money = Integer.valueOf(params[1]);
		int[] items = new int[size];
		
		// Parse price tags and sort
		line = reader.nextLine();
		String[] tags = line.split(" ");
		for(int i=0; i < size; i++) {
			items[i] = Integer.valueOf(tags[i]);
		}
		Arrays.sort(items);
		
		// Calculate what we can buy
		int amount = calculateMax(money, items);
		
		String out = Integer.valueOf(amount) + "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
	
	/*
	 * We buy all the small toys first and work our way up
	 * till we have no more money left.
	 */
	private int calculateMax(final int money, final int[] tags) {
		int amount = 0;
		int remaining = money;
		for(int i=0; i < tags.length; i++) {
			if(remaining >= tags[i]) {
				amount++;
				remaining -= tags[i];
			}
			else { break; } // No more money
		}

		return amount;
	}
}
