package crush;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		String[] params = line.split(" ");
		int queries = Integer.valueOf(params[1]);
		
		HashMap<Integer, ArrayList<Long>> map = new HashMap<>();
		HashMap<Integer, Result> totals = new HashMap<>();
		int maxIndex = 0;
		Result maxResult = new Result(0L, 0L);
		
		int start, stop;
		long amount;
		for(int i=0; i < queries; i++) {
			line = reader.nextLine();
			params = line.split(" ");
			start = Integer.valueOf(params[0]);
			stop = Integer.valueOf(params[1]);
			amount = Integer.valueOf(params[2]);
			
			for(int j=start-1; j <= stop-1; j++) {
				// Add the op to history
				ArrayList<Long> ops = map.get(j)==null ? new ArrayList<Long>() : map.get(j);
				ops.add(amount);
				map.put(j, ops);
				
				// Add the amount
				Result r = (totals.get(j)==null) ? new Result(0L,0L) : totals.get(j);
				r = add(r, amount);
				totals.put(j, r);
				
				// See if we need to update max
				if(r.overflowCount >= maxResult.overflowCount) {
					if(r.val >= maxResult.val) {
						maxResult = r;
						maxIndex = j;
					}
				}
			}
		}
		
		// Calc the max
		BigInteger cur = BigInteger.ZERO;
		for(long op : map.get(maxIndex)) {
			cur = cur.add(BigInteger.valueOf(op));
		}
		
		String out = cur.toString() + "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
	
	private Result add(Result cur, long b) {
		long result = cur.val + b;
		cur.overflowCount = result < cur.val ? cur.overflowCount+1 : cur.overflowCount;
		cur.val = result;
		return cur;
	}
	
	class Result {
		public long val;
		public long overflowCount;
		public Result(long val, long overflowCount) {
			this.val = val;
			this.overflowCount = overflowCount;
		}
	}
}
