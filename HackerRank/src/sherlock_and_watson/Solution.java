package sherlock_and_watson;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/*
 * Sherlock and Watson
 * 
 * Passes all tests
 */
public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		
		String[] params = line.split(" ");
		int size = Integer.valueOf(params[0]);
		int shift = Integer.valueOf(params[1]);
		int queries = Integer.valueOf(params[2]);
		
		String str_array = reader.nextLine();
		int[] array = buildArray(str_array, shift, size);
		
		String out = "";
		for(int i=0; i < queries; i++) {
			line = reader.nextLine();
			int index = Integer.valueOf(line);
			out = Integer.toString(array[index]) + "\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	/*
	 * Build the array based on the shift
	 */
	private int[] buildArray(String s, int shift, int size) {
		int[] array = new int[size];
		
		Scanner scan = new Scanner(s);
		scan.useDelimiter(" ");
		
		for(int i=0; i < size; i++) {
			int index = (i+shift) % size; // Determine shifted index
			array[index] = scan.nextInt();
		}
		scan.close();
		
		return array;
	}
	
	
}
