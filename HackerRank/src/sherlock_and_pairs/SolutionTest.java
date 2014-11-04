package sherlock_and_pairs;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;


public class SolutionTest {

	private StringArrayReader reader = null;
	private StringArrayWriter writer = null;
	
	@Before
	public void setUp() throws Exception {
		reader = new StringArrayReader();
		writer = new StringArrayWriter();
	}
	
	@Test
	public void test0() throws IOException {
		File file = new File("./bin/sherlock_and_pairs/test0_in.txt");
		reader.add(file);
		
		run(reader, writer);
		
		// Check from test case output
		File check = new File("./bin/sherlock_and_pairs/test0_out.txt");
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(check)));
		String line = r.readLine();
		int c = 0;
		while(line != null) {
			assertEquals(line, writer.remove());
			line = r.readLine();
		}
		r.close();
	}
	
	@Test
	public void test11() throws IOException {
		File file = new File("./bin/sherlock_and_pairs/test11_in.txt");
		reader.add(file);
		
		run(reader, writer);
		
		// Check from test case output
		File check = new File("./bin/sherlock_and_pairs/test11_out.txt");
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(check)));
		String line = r.readLine();
		int c = 0;
		while(line != null) {
			System.out.println(++c + ": " + line);
			assertEquals(line, writer.remove());
			line = r.readLine();
			System.out.println("OK!");
		}
		r.close();
	}
	
	private void run(StringArrayReader reader, StringArrayWriter writer) throws IOException {
		new Solution().run(new Scanner(reader), new PrintStream(writer));
	}
	
	class StringArrayReader extends InputStream {
		ArrayList<String> inputs = new ArrayList<String>();
		byte[] current;
		int place = 0;
		
		public void add(String str) {
			inputs.add(str);
		}
		
		public void add(File file) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = reader.readLine();
			while(line != null) {
				add(line);
				line = reader.readLine();
			}
			reader.close();
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
			for(String line : str.split("\n")) {
				outputs.add(line);
			}
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
