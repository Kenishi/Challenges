package crush;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class Solution2 {

	public static void main(String[] args) throws IOException {
		new Solution2().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		String[] params = line.split(" ");
		int size = Integer.valueOf(params[0]);
		int queries = Integer.valueOf(params[1]);
		
		BigInteger[] vals = new BigInteger[size];
		Arrays.fill(vals, BigInteger.ZERO);
		
		BigInteger max = BigInteger.valueOf(0);
		int start, stop, amount;
		for(int i=0; i < queries; i++) {
			line = reader.nextLine();
			params = line.split(" ");
			start = Integer.valueOf(params[0]);
			stop = Integer.valueOf(params[1]);
			amount = Integer.valueOf(params[2]);
			
			for(int j=start-1; j <= stop-1; j++) {
				vals[j] = vals[j].add(BigInteger.valueOf(amount));
				if(vals[j].compareTo(max) == 1) {
					max = vals[j];
				}
			}
		}
		
		for(int i=0; i < vals.length; i++) {
			System.out.println(i + ": " + vals[i].toString());
		}
		
		String out = max.toString() + "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
}
