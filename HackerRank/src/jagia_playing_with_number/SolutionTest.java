package jagia_playing_with_number;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	private StringArrayReader reader;
	private StringArrayWriter writer;
	
	@Before
	public void setUp() throws Exception {
		reader = new StringArrayReader();
		writer = new StringArrayWriter();
	}

	@Test
	public void test() throws IOException {
		reader.add("2");
		reader.add("U 692778 7 291188");
		reader.add("R 636916 747794");
		
		new Solution().run(new Scanner(reader), new PrintStream(writer));
		
		assertEquals("378\n", writer.remove());
	}
	
	@Test
	public void testcase6() throws IOException {
		File f = new File("./bin/jagia_playing_with_number/testcase6_input");
		reader.add(f);
		
		new Solution().run(new Scanner(reader), new PrintStream(writer));
		
		// Check from test case output
		File check = new File("./bin/jagia_playing_with_number/testcase6_output");
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(check)));
		String line = r.readLine();
		while(line != null) {
			assertEquals(line+"\n", writer.remove());
			line = r.readLine();
		}
		r.close();
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
