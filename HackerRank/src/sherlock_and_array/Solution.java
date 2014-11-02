package sherlock_and_array;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/*
 * Sherlock and Arrays
 * 
 * Passes all tests
 */

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int cases = Integer.valueOf(line);
		
		for(int i=0; i < cases; i++) {
			line = reader.nextLine();
			int size = Integer.valueOf(line);
			int[] array = new int[size];
			
			// Read in array
			line = reader.nextLine();
			Scanner scan = new Scanner(line);
			scan.useDelimiter(" ");
			for(int j=0; j < size; j++) {
				array[j] = scan.nextInt();
			}
			scan.close();
						
			String out = hasSum(array) ? "YES\n" : "NO\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	private boolean hasSum(int[] a) {
		int l_index = 0;
		int r_index = a.length-1;
		int l_sum = a[l_index];
		int r_sum = a[r_index];
		
		// Handle small size cases
		if(a.length <= 1) { // Sum is 0 on both sides
			return true;
		}
		
		/*
		 * Move toward till meet
		 */
		while(r_index != l_index) {
			if(r_sum > l_sum) {
				l_sum += a[++l_index];
			}
			else if(r_sum <= l_sum) {
				r_sum += a[--r_index];
			}
		}
		
		// Remove meeting point value from each sum
		int i = r_index;
		r_sum -= a[i];
		l_sum -= a[i];
		
		// Shift till we either balance or the other side becomes
		// greater
		if(r_sum > l_sum) {
			while(r_sum > l_sum && i < a.length) {
				r_sum -= a[i+1];
				l_sum += a[i];
				i++;
			}
		}
		else if(l_sum > r_sum) {
			while(l_sum > r_sum && i >= 0) {
				l_sum -= a[i-1];
				r_sum += a[i];
				i--;
			}
		}
		
		// Check
		if(l_sum == r_sum) {
			return true;
		}
		else {
			return false;
		}
	}
}
