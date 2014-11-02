package running_time_of_alg;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/*
 * Running Time of Algorithms Problem
 * 
 * Passes all Test Cases
 */

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int size = Integer.valueOf(line);
		int[] array = new int[size];

		// Get array
		line = reader.nextLine();
		String[] vals = line.split(" ");
		for(int i=0; i < size; i++) {
			array[i] = Integer.valueOf(vals[i]);
		}
		
		String out = Integer.toString(insertSortShiftCount(array));
		writer.write(out.getBytes());
		writer.flush();
	}
	
	public int insertSortShiftCount(int[] array) {
		int shiftCount = 0;
		
		for(int sortIndex=1; sortIndex < array.length; sortIndex++) {
			int valToSort = array[sortIndex];
			
			for(int searchIndex=sortIndex; searchIndex > 0; searchIndex--) {
				if(valToSort >= array[searchIndex-1]) {
					break; // Sorted
				}
				else {
					array[searchIndex] = array[searchIndex-1]; // Shift
					array[searchIndex-1] = valToSort;					
					shiftCount++;
				}
			}
		}
		return shiftCount;
	}
}