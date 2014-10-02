package quora.solutions.feed;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class QuoraFeedv2Test {
	StringArrayReader reader;
	StringArrayWriter writer;
	Random rand;

	/** Vars for Random/Load test **/
	int time = 1;
	
	@Before
	public void setUp() throws Exception {
		reader = new StringArrayReader();
		writer = new StringArrayWriter();
		rand = new Random();
		time = 1;
	}
		
	@Test
	public void addTest() throws NumberFormatException, IOException {		
		reader.add("2 10 100");
		reader.add("S 11 50 30");
		reader.add("R 12");
		
		new Solution().run(new Scanner(reader), new PrintStream(writer));
		
		assertEquals("50 1 1\n", writer.remove());
	}
	
	@Test
	public void noAddRefresh() throws NumberFormatException, IOException {
		
		reader.add("1 10 100");
		reader.add("R 10");
		
		new Solution().run(new Scanner(reader), new PrintStream(writer));
	}
	
	@Test
	public void test2() throws NumberFormatException, IOException {
		reader.add("83 120 13");
		reader.add("S	10	100	1");
		reader.add("S	11	100	1");
		reader.add("S	12	100	1");
		reader.add("S	13	100	1");
		reader.add("S	14	100	1");
		reader.add("S	15	100	1");
		reader.add("S	16	100	1");
		reader.add("S	17	100	1");
		reader.add("S	18	100	1");
		reader.add("S	19	100	1");
		reader.add("S	20	100	1");
		reader.add("S	21	100	1");
		reader.add("S	22	100	1");
		reader.add("S	23	100	1");
		reader.add("S	24	100	1");
		reader.add("S	25	100	1");
		reader.add("S	26	100	1");
		reader.add("S	27	100	1");
		reader.add("S	28	100	1");
		reader.add("S	29	100	1");
		reader.add("S	30	100	1");
		reader.add("S	31	100	1");
		reader.add("S	32	100	1");
		reader.add("S	33	100	1");
		reader.add("S	34	100	1");
		reader.add("S	35	100	1");
		reader.add("S	36	100	1");
		reader.add("S	37	100	1");
		reader.add("S	38	100	1");
		reader.add("S	39	100	1");
		reader.add("S	40	100	1");
		reader.add("S	41	100	1");
		reader.add("S	42	100	1");
		reader.add("S	43	100	1");
		reader.add("S	44	100	1");
		reader.add("S	45	100	1");
		reader.add("S	46	100	1");
		reader.add("S	47	100	1");
		reader.add("S	48	100	1");
		reader.add("S	49	100	1");
		reader.add("S	50	100	1");
		reader.add("S	51	100	1");
		reader.add("S	52	100	1");
		reader.add("S	53	100	1");
		reader.add("S	54	100	1");
		reader.add("S	55	100	1");
		reader.add("S	56	100	1");
		reader.add("S	57	100	1");
		reader.add("S	58	100	1");
		reader.add("S	59	100	1");
		reader.add("S	60	100	1");
		reader.add("S	61	100	1");
		reader.add("S	62	100	1");
		reader.add("S	63	100	1");
		reader.add("S	64	100	1");
		reader.add("S	65	100	1");
		reader.add("S	66	100	1");
		reader.add("S	67	100	1");
		reader.add("S	68	100	1");
		reader.add("S	69	100	1");
		reader.add("S	70	100	1");
		reader.add("S	71	100	1");
		reader.add("S	72	100	1");
		reader.add("S	73	100	1");
		reader.add("S	74	100	1");
		reader.add("S	75	100	1");
		reader.add("S	76	100	1");
		reader.add("S	77	100	1");
		reader.add("S	78	100	1");
		reader.add("S	79	100	1");
		reader.add("S	80	100	1");
		reader.add("S	81	100	1");
		reader.add("S	82	100	1");
		reader.add("S	83	100	1");
		reader.add("S	84	100	1");
		reader.add("S	85	100	1");
		reader.add("S	86	100	1");
		reader.add("S	87	100	1");
		reader.add("S	88	100	1");
		reader.add("S	89	100	1");
		reader.add("S	90	100	1");
		reader.add("S	91	100	1");
		reader.add("R 92");
		
		new Solution().run(new Scanner(reader), new PrintStream(writer));
		
		assertEquals("1300 13 1 2 3 4 5 6 7 8 9 10 11 12 13\n", writer.remove());
	}
	
	@Test
	public void R21WebTest() throws NumberFormatException, IOException {
	
		reader.add("5 10 100");
		reader.add("S 11 50 30");
		reader.add("S 13 40 20");
		reader.add("S 14 45 40");
		reader.add("S 18 45 20");
		reader.add("R 21");

		new Solution().run(new Scanner(reader), new PrintStream(writer));
		
		assertEquals("140 3 1 3 4\n", writer.remove());
	}
	@Test
	public void webTestCase() throws NumberFormatException, IOException {
		reader.add("9 10 100");
		reader.add("S 11 50 30");
		reader.add("R 12");
		reader.add("S 13 40 20");
		reader.add("S 14 45 40");
		reader.add("R 15");
		reader.add("R 16");
		reader.add("S 18 45 20");
		reader.add("R 21");
		reader.add("R 22");
		
		new Solution().run(new Scanner(reader), new PrintStream(writer));
		
		assertEquals("50 1 1\n", writer.remove());
		assertEquals("135 3 1 2 3\n", writer.remove());
		assertEquals("135 3 1 2 3\n", writer.remove());
		assertEquals("140 3 1 3 4\n", writer.remove());
		assertEquals("130 3 2 3 4\n", writer.remove());	
	}
	
	@Test
	public void loadTest() throws NumberFormatException, IOException {
		int eventCount = 10000;
		int timeWindow = 2000;
		int maxHeight = 2000;
		int maxScore = 500;

		
		String initialVars = Integer.toString(eventCount) + " " +
				Integer.toString(timeWindow) + " " + Integer.toString(maxHeight);
		reader.add(initialVars);
		
		boolean isStory;
		for(int i=0; i < eventCount; i++) {
			if((i % 500) == 0) {
				isStory = false;
			}
			else {
				isStory = isStory();
			}
			String out = isStory ? nextStory(maxHeight, maxScore) : nextRefresh();
			reader.add(out);
		}
		
		new Solution().run(new Scanner(reader), new PrintStream(writer));
		
	}
	
	public String nextStory(int maxHeight, int maxScore) {
		return "S " + nextTime() + " " + nextScore(maxScore) + " " + nextHeight(maxHeight);
	}
	
	public String nextRefresh() {
		return "R " + nextTime();
	}
	
	public String nextHeight(int max) { 
		return Integer.toString(rand.nextInt(max+1)+1);
	}
	
	public String nextScore(int max) {
		return Integer.toString(rand.nextInt(max+1));
	}
	
	public String nextTime() {
		if(rand.nextBoolean()) { // Large time advance
			time += rand.nextInt(60+1);
		}
		else { // Small time advance
			time += rand.nextInt(5+1);
		}
		return Integer.toString(time);
	}
	
	public boolean isStory() {
		return rand.nextBoolean();
	}

	public void dumpOutput(StringArrayWriter writer) {
		String out = writer.remove();
		while(out != null) {
			System.out.println(out);
			out = writer.remove();
		}
	}
	public byte[] toBytes(String str) {
		return str.getBytes();
	}
	
	public void write(String str) throws IOException {
		System.out.write(str.getBytes());
		System.out.flush();
	}
	
	class StringArrayReader extends InputStream {
		ArrayList<String> inputs = new ArrayList<String>();
		byte[] current;
		int place = 0;
		
		public void add(String str) {
			inputs.add(str);
		}

		@Override
		public int read() throws IOException {
			if(current == null) {
				if(inputs.size() <= 0) { return -1; }
				
				current = inputs.remove(0).getBytes();
				place = 0;
			}
			else if(place >= current.length) {
				if(inputs.size() <= 0) { return -1; }
				
				current = inputs.remove(0).getBytes();
				place = 0;
				return '\n';
			}
			return current[place++];
		}
	}
	
	class StringArrayWriter extends OutputStream {
		ArrayList<String> outputs = new ArrayList<String>();
		byte[] buffer = new byte[1024];
		int cur = 0;
		
		@Override
		public void flush() throws IOException {
			if(cur == 0) {
				outputs.add("");
				return;
			}
			String str = new String(buffer, 0, cur);
			outputs.add(str);
			cur = 0;
		}
		
		@Override
		public void write(int b) throws IOException {
			buffer[cur] = (byte) (b & 0xFF);
			cur++;
		}
		
		public String remove() {
			if(outputs.size() <= 0) return null;
			return outputs.remove(0);
		}
	}

}
