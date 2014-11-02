package insertion_sort;

import java.io.PrintStream;
import java.util.Scanner;

/*
 * Basic Insertion Sort template
 * 
 */

public class Solution {

	public static void main(String[] args) {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) {
		String line = reader.nextLine();
		int size = Integer.valueOf(line);
		int[] array = new int[size];

		// Get array
		line = reader.nextLine();
		String[] vals = line.split(" ");
		for(int i=0; i < size; i++) {
			array[i] = Integer.valueOf(vals[i]);
		}
		
		array = insertSort(array);
	}
	
	public int[] insertSort(int[] array) {
		for(int sortIndex=1; sortIndex < array.length; sortIndex++) {
			int valToSort = array[sortIndex];
			
			for(int searchIndex=sortIndex; searchIndex > 0; searchIndex--) {
				if(valToSort >= array[searchIndex-1]) {
					break; // Sorted
				}
				else {
					array[searchIndex] = array[searchIndex-1]; // Shift
					array[searchIndex-1] = valToSort;					
				}
			}
		}
		return array;
	}
}
