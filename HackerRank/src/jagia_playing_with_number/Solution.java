package jagia_playing_with_number;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class Solution {
	private int[] buckets = new int[1000000];
	
	
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), new PrintStream(System.out));
	}
	
	public void run(Scanner read, PrintStream write) throws IOException {
		// Get event count
		if(!read.hasNext()) return;
		String line = read.nextLine();
		int count = Integer.parseInt(line);
		
		String[] cmd = null;
		for(int i=0; i < count && read.hasNext(); i++) {
			line = read.nextLine();
			cmd = line.split(" ");
			if(cmd[0].toLowerCase().equals("u")) {
				int pos = Integer.parseInt(cmd[1]);
				int amount = Integer.parseInt(cmd[2]);
				int skip = Integer.parseInt(cmd[3]);
				updateTest(pos, amount, skip);
			}
			else if(cmd[0].toLowerCase().equals("r")) {
				int start = Integer.parseInt(cmd[1]);
				int stop = Integer.parseInt(cmd[2]);
				long amount = query(start, stop);
				String out = Long.toString(amount) + "\n"; 
				write.write(out.getBytes());
				write.flush();
			}
		}
	}
	
	public void add(int pos, int val) {
		if(pos > 1000000 || pos < 1) throw new ArrayIndexOutOfBoundsException();
		buckets[pos-1] += val;
	}
	
	public int get(int pos) {
		if(pos > 1000000 || pos < 1) throw new ArrayIndexOutOfBoundsException();
		return buckets[pos-1];
	}
	
	public long query(int start, int stop) {
		start = (start < 0) ? 0 : start;
		stop = (stop >= buckets.length) ? buckets.length-1 : stop; 
		
		long count = 0;
		for(int i=start; i <= stop; i++) {
			count += get(i);
		}
		return count;
	}
	
	public void updateTest(int p, int amount, int skip) {
		int N = 1000000;
		
		for(int i=1; i <= N; i++) {
			int pos = 1;
			for(int j=1; j <= 3; j++) {
				int m=pos;
				int s,in = PopCount.popcount(pos);
				for(int k=0;;k++) {
					s = pos + pow2(k);
					if(s <= in) {
						in = PopCount.popcount(s);
						pos = s;
						m += m & (-m);
						assert m == pos;
						if(pos > N) break;
					}
				}
				pos = pos - N;
				System.out.print(pos + " ");
			}
		}
		System.out.println("\n");
	}
	
	static int process = 0;
	public void update(int pos, int amount, int skip) {
		//long time_in = (new Date()).getTime();
		int N = 1000000;
		int s, cutoff_max = 0;
		for(int i=1; i <= N; i++) {
			int start = pos; // Current start position for this iteration
			int m = pos;
			for(int j=1; j<= 3; j++) {
				/* 
				 * The cutoff [max] number of set bits an index can have
				 * in order to be set.
				 * This will shrink on each set. 
				 */
				cutoff_max = popcount(pos);
				for(int k=0;;k++) {
					s = (int) (pos + pow2(k)); //pos + 2^k
					int pop_s = popcount(s);
					
					if(pop_s <= cutoff_max) {
						cutoff_max = pop_s; // Set to pos + 2^k set bitcount 
						pos = s; // Set to pos + 2^k
						m += m & (-m);
						if(pos > N) break; // Avoid array out of bound
						add(pos, m);
					}
					if(k > 18) { System.out.println(k); }
				}
				
				pos -= N;
			}
			pos = start + skip; // Move ahead
			if(pos > N) pos-=N; // Roll over to start of array again
		}
		//long time_out = (new Date()).getTime();
		//System.out.println("Process Time:" + (time_out-time_in));
	}
	
	private int popcount(int pos) {
		return PopCount.popcount(pos);
	}
	
	private int pow2(int y) {
		return (y==0) ? 1 : 1 << y;
	}
		
	private static class PopCount extends LinkedHashMap<Integer, Integer> {
		private final int MAX_SIZE = 100000;
		private static final PopCount m_instance = new PopCount();
		
		public static int popcount(int val) {
			if(val == 0) return 0;
			if(m_instance.containsKey(val)) {
				return m_instance.get(val);
			}
			else {
				int max_bits = getBitSize(val);
				int current = val;
				int count = (current & 1);
				for(int i=0; i < max_bits; i++) {
					current = current >> 1;
					count += (current & 1);
				}
				m_instance.put(val, count);
				return count;
			}
		}
		
		private static int getBitSize(int n) {
			if(n <= 0) return 0;
			else {
				return 31 - Integer.numberOfLeadingZeros(n); 
			}
		}
		
		@Override
		protected boolean removeEldestEntry(
				java.util.Map.Entry<Integer, Integer> eldest) {
			return (size() > MAX_SIZE) ? true : false;
		}
	}
}
