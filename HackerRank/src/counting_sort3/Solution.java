package counting_sort3;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/*
 * Counting sort 3
 */
public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int count = Integer.valueOf(line);
		int[] array = new int[100];
		
		for(int i=0; i < count; i++) {
			line = reader.nextLine();
			String[] split = line.split(" ");
			int val = Integer.valueOf(split[0]);
			array[val]++;
		}
		
		String out = "";
		int tally = 0;
		for(int i=0; i < 100; i++) {
			tally += array[i];
			out += Integer.toString(tally) + " ";
		}
		out = out.trim() + "\n";
		
		writer.write(out.getBytes());
		writer.flush();
	}
}
