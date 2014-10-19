package xor_and_sum;

import java.math.BigInteger;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Solution {
	private ThreadPoolExecutor pool;
	private AtomicBigInteger sum = new AtomicBigInteger();
	
	public Solution() {
		ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10000);
		pool = new ThreadPoolExecutor(10, 20, 15, TimeUnit.SECONDS, queue);
	}
	
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
	
	public void result(BigInteger val) {
		sum.add(val);
	}
		
	private long process(String a_str, String b_str) {
		BigInteger a = new BigInteger(a_str, 2);
		BigInteger b = new BigInteger(b_str, 2);
		
		BigInteger mod = new BigInteger("1000000007");
		for(int i=0; i <= 314159;) {
			try {
				pool.execute(new Calc(a, b, i));
				i++;
			} catch(RejectedExecutionException e) {
				try {
					Thread.sleep(5);
				} catch(InterruptedException f) {}
			}
		}
		
		pool.shutdown();
		try {
			pool.awaitTermination(15, TimeUnit.SECONDS);
		} catch(InterruptedException e) {}
		
		long ret = sum.get().mod(mod).longValue(); 
		return ret;
	}
	
	class Calc implements Runnable {
		private BigInteger m_a, m_b;
		private int m_i;
		
		public Calc(BigInteger a, BigInteger b, int i) {
			m_a = a;
			m_b = b;
			m_i = i;
		}
		
		public void run() {
			// Shift left
			BigInteger shl = m_b.shiftLeft(m_i);
			// XOR
			BigInteger xor = m_a.xor(shl);
			result(xor);
		}
	}
	
	class AtomicBigInteger {
		AtomicReference<BigInteger> i = new AtomicReference<BigInteger>();
		
		public AtomicBigInteger() {
			i.set(new BigInteger("0"));
		}
		
		public void add(BigInteger val) {
			for(;;) {
				BigInteger cur = i.get();
				BigInteger sum = cur.add(val);
				if(i.compareAndSet(cur, sum)) {
					return;
				}
			}
		}
		
		public BigInteger get() {
			return i.get();
		}
	}
}
