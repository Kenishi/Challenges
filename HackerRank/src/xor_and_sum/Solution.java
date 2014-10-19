package xor_and_sum;

import java.math.BigInteger;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Solution {
	
	public static void main(String[] args) {
		new Solution().run();
	}
	
	public void run() {
		Scanner scanner = new Scanner(System.in);
		String a = scanner.nextLine();
		String b = scanner.nextLine();
		scanner.close();
		long start = new Date().getTime();
		long val = process(a,b);
		long stop = new Date().getTime();
		System.out.println(val);
		System.out.println("Done in: " + (stop-start) + "ms");
	}
		
	private long process(String a_str, String b_str) {
		BigInteger a = new BigInteger(a_str, 2);
		BigInteger b = new BigInteger(b_str, 2);
		
		BigInteger shl, xor;
		BigInteger sum = new BigInteger("0");
		BigInteger mod = new BigInteger("1000000007");
		for(int i=0; i <= 314159; i++) {
			// Shift left
			shl = b.shiftLeft(i);
			// XOR
			xor = a.xor(shl);
			// Sum
			sum = sum.add(xor);
		}
		long ret = sum.mod(mod).longValue(); 
		return ret;
	}
}
