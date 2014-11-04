package bus_station;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Boarding Station Solution
 * 
 * Passes all tests
 * 
 * This one took a few tries but I finally
 * got it without having to check the editorial.
 * 
 * First I tried a brute force method and that passed
 * a fourth of the tests but timed out on the rest.
 * 
 * Then I tried a solution where possible sizes are
 * checked as the numbers are read in. Still timeout.
 * 
 * Possible sizes can be figured by summing the group
 * amounts as you read them in and storing each new group
 * as a possible size. 
 * Ex: 1 2 4 6 (These are group inputs)
 * Possible sizes: 1 3 (1+2) 7 (1+2+4) 13 (1+2+4+6)
 * 
 * Finally I realized that the answer is always divisible
 * by the max size. So I read in the numbers, storing each
 * potential size, then do a check where I take each size
 * and test if its evenly divisible by max. For the example,
 * nothing is divisible by 13. (ex: 13 mod 2 = 1, etc.. )
 * 
 */

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int count = Integer.valueOf(line);
		
		line = reader.nextLine();
		String[] vals = line.split(" ");
		
		int[] groups = new int[count];
		int maxBusSize = 0;
		
		ArrayList<Integer> possSizes = new ArrayList<>();
		
		for(int i=0; i < count; i++) {
			groups[i] = Integer.valueOf(vals[i]);
			maxBusSize += groups[i];
			possSizes.add(maxBusSize);
			
		}
		
		String out = getSizes(possSizes, groups);
		
		writer.write(out.getBytes());
		writer.flush();
	}
	
	private String getSizes(ArrayList<Integer> sizes, int[] groups) {
		String out = "";
		int maxSize = sizes.remove(sizes.size()-1);
		for(int size : sizes) {
			if((maxSize % size) != 0) { continue; }
			else {
				if(check(size, groups)) {
					out += size + " ";
				}
			}
		}
		out += maxSize + "\n";
		return out;
	}
	
	private boolean check(int size, int[] groups) {
		boolean isOk = true;
		int remain = size;
		for(int i=0; i < groups.length && isOk; i++) {
			remain -= groups[i];
			if(remain == 0) { remain = size; }
			else if(remain < 0) { 
				isOk = false;
			}
		}
		return isOk;
	}
}
