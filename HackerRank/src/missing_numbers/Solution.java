package missing_numbers;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.TreeMap;

public class Solution {
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		int aSize = Integer.valueOf(reader.nextLine()); // A size
		String a = reader.nextLine();
		int bSize = Integer.valueOf(reader.nextLine()); // B size
		String[] params = reader.nextLine().split(" "); // B list
		
		// Get B list freq count
		TreeMap<Integer, Integer> bFreq = new TreeMap<>();
		for(int i=0; i < bSize; i++) {
			Integer count = bFreq.get(Integer.valueOf(params[i]));
			count = count == null ? 1 : (count + 1);
			bFreq.put(Integer.valueOf(params[i]), count);
		}
		
		params = a.split(" ");
		// Get A list freq count
		for(int i=0; i < aSize; i++) {
			Integer count = bFreq.get(Integer.valueOf(params[i]));
			count--;
			bFreq.put(Integer.valueOf(params[i]), count);
		}
		
		// Check what's missing
		String out = "";
		for(Integer key : bFreq.keySet()) {
			int count = bFreq.get(key);
			out = (count > 0) ? out + key + " " : out;
		}
		out += "\n";
		
		writer.write(out.getBytes());
		writer.flush();
	}
}
