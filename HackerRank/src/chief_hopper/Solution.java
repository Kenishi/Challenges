package chief_hopper;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int size = Integer.valueOf(line);
		
		BigInteger val = BigInteger.ZERO;
		BigInteger pVal = BigInteger.valueOf(2);
		for(int i=0, pow = size-1; i < size; i++, pow--) {
			val = val.add(pVal.pow(pow).multiply(reader.nextBigInteger()));
		}
		BigInteger[] res = val.divideAndRemainder(pVal.pow(size));
		val = res[1].equals(BigInteger.ZERO) ? res[0] : res[0].add(BigInteger.ONE);
		
		String out = val + "\n";
		writer.write(out.getBytes());
		writer.flush();
	}
}
