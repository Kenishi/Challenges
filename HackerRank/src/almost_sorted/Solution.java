package almost_sorted;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/*
 * Almost Sorted
 * 
 * Passes all tests.
 * 
 * This required a few submissions and lots of tweaking to get right
 * 
 */
public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		int size = Integer.valueOf(reader.nextLine());
		
		String str_array = reader.nextLine();
		
		String result = isSortable(str_array, size);
		
		writer.write(result.getBytes());
		writer.flush();
	}
	
	private String isSortable(String str_array, int size) {
		String out = "";
		
		Scanner scan = new Scanner(str_array);
		scan.useDelimiter(" ");
		ArrayList<Integer> wrongOrder = new ArrayList<>();

		int[] array = new int[size+1];
		int i = 0;
				
		while(scan.hasNextInt()) {
			array[++i] = scan.nextInt();
		}
		scan.close();
		
		for(i=size; i > 0; i--) {
			if(array[i] < array[i-1]) {
				// Look for edge
				int edge = i;
				for(int j=i; j > 0; j--) {
					if(array[j] > array[j-1]) {
						// Check if start fits in spot
						if(array[i] > array[j-1] && array[i] < array[j]) {
							edge = j;
							break;
						}
					}
					else if(array[j] < array[j-1]) {
						wrongOrder.add(j); // Handle wrong order numbers in-between edge
					}
				}
				// Edge could have moved or could still be same spot
				wrongOrder.add(edge);
				i=edge;
			}
		}
		
		Collections.sort(wrongOrder); // Make it ascending indexs
		
		// If wrongOrder is size 2, might be swap
		if(wrongOrder.size() == 2) {
			int a = wrongOrder.get(0);
			int b = wrongOrder.get(1);

			out = trySwap(array, a, b) ? "yes\nswap " + wrongOrder.get(0) + " " + wrongOrder.get(1) : "no";
		}
		else if(wrongOrder.size() == 0) { // Already ordered
			out = "yes";
		}
		else {
			// Look for continuity for a reverse
			boolean hasCont = hasContinuity(wrongOrder);
			
			// This covers cases such as
			// Ex: 1 4 3 2
			// Ex: 1 2 80 4 5 .. 79 3 81 .. / 80, 4, 3 are flagged wrongOrder
			if(wrongOrder.size() == 3) {
				out = trySwap(array, wrongOrder.get(0), wrongOrder.get(2)) ? "yes\nswap " + wrongOrder.get(0) + " " + wrongOrder.get(2) : "no";
			}
			else if(hasCont) {
				/*
				 * Get the indexes of the numbers to swap
				 * Get start index and start+1 index
				 * Get end index and end-1 index
				 * 
				 * Note: Reverses can only happen on sets of size > 4
				 */
				int a = wrongOrder.get(0);
				int b = wrongOrder.get(wrongOrder.size()-1);
				
				// Check fits
				out = tryReverse(array, a, b) ? "yes\nreverse " + wrongOrder.get(0) + " " + wrongOrder.get(wrongOrder.size()-1) : "no";
			}
			else {
				out = "no";
			}
		}
		
		return out + "\n";
	}
	
	private boolean trySwap(int[] array, int a, int b) {
		// Do the switch
		int temp = array[a];
		array[a] = array[b];
		array[b] = temp;
		
		// Check fits
		return fits(array, a) && fits(array, b);
	}
	
	private boolean tryReverse(int[] array, int start, int stop) {				
		boolean fits = true;
		for(int i=start, r=stop; i <= r; i++, r--) {
			int a = array[r]; // New a will be value at end
			int b = array[i]; // New b will be value at front
			
			// Swap
			array[i] = a;
			array[r] = b;
			
			if(!fits) { break; }
			else {
				fits = fits(array, i) && fits(array, r);
			}
		}
		
		// Check fits
		return fits;
	}

	private boolean fits(int[] array, int index) {

		/*
		 * Number at index fits if its greater than the prior number.
		 * If the prior index would be 0, then it fits in the order following previous
		 */
		boolean fits = index-1 != 0 ? (array[index] > array[index-1]) : true;
		
		/*
		 * If the number at index fitted following the order previous to it then check after
		 * If this isn't the end of the array, then check that the number after is greater
		 * If it's at the end then it fits if it fitted in the previous order
		 */
		fits = fits && index+1 != array.length ? (array[index] < array[index+1]) : fits;
		
		return fits;
	}
	
	private boolean hasContinuity(ArrayList<Integer> wrongOrder) {
		int prev = -1;
		boolean hasCont = false;
		for(int index : wrongOrder) {
			if(prev == -1) {
				prev = index;
				continue;
			}
			else {
				if(index-1 == prev) {
					hasCont = true;
				}
				else {
					hasCont = false;
					break;
				}
				prev = index;
			}
		}
		return hasCont;
	}
}
