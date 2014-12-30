import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import TypeSuggest.IllegalQueryException;
import TypeSuggest.UnexpectedStateException;

public class TypeSuggestTest {
	StringArrayWriter writer;
	StringArrayReader reader;

	
	@Before
	public void setUp() throws Exception {
		// Setup data streams
		writer = new StringArrayWriter();
		reader = new StringArrayReader();
	}
	
	@Test(expected=IllegalQueryException.class)
	public void noActionTest() throws IOException {
		reader.add("1");
		reader.add("LIST");
		
		TypeSuggest.run(new Scanner(reader), new PrintStream(writer));
	}
	
	@Test(expected=UnexpectedStateException.class)
	public void invalidDataTypeTest() throws IOException {
		reader.add("1");
		reader.add("ADD answer a1 1.0 Adam");
		
		TypeSuggest.run(new Scanner(reader), new PrintStream(writer));
	}
	
	@Test(expected=UnexpectedStateException.class)
	public void recursionLimitTest() throws IOException {
		reader.add("1");
		reader.add("ADD user u1 1.0 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		
		TypeSuggest.run(new Scanner(reader), new PrintStream(writer));
	}
	
	@Test
	public void doubleDeleteTest() throws IOException {
		reader.add("3");
		reader.add("ADD user u1 1.0 Adam");
		reader.add("DEL u1");
		reader.add("DEL u1");
		
		TypeSuggest.run(new Scanner(reader), new PrintStream(writer));
	}
	
	@Test(expected=UnexpectedStateException.class)
	public void wQueryInvalidBoostTypeTest() throws IOException {
		reader.add("1");
		reader.add("WQUERY 10 1 read:8.00");
		
		TypeSuggest.run(new Scanner(reader), new PrintStream(writer));
	}
	
	@Test(expected=IllegalQueryException.class)
	public void noEventCountTest() throws IOException {
		reader.add("ADD user u1 1.0 John doe");
		
		TypeSuggest.run(new Scanner(reader), new PrintStream(writer));
	}
	
	@Test
	public void webSampleTest() throws IOException {
		reader.add("15");
		reader.add("ADD user u1 1.0 Adam D'Mero");
		reader.add("ADD user u2 1.0 Adam Black");
		reader.add("ADD topic t1 0.8 Adam D'Mero");
		reader.add("ADD question q1 0.5 What does Adam D'Mero do at Marvel?");
		reader.add("ADD question q2 0.5 How did Adam D'Mero learn draw?");
		reader.add("QUERY 10 Adam");
		reader.add("QUERY 10 Adam D'M");
		reader.add("QUERY 10 Adam Cheever");
		reader.add("QUERY 10 LEARN how");
		reader.add("QUERY 1 lear H");
		reader.add("QUERY 0 lea");
		reader.add("WQUERY 10 0 Adam D'M");
		reader.add("WQUERY 2 1 topic:9.99 Adam D'M");
		reader.add("DEL u2");
		reader.add("QUERY 2 Adam");
			
		TypeSuggest.run(new Scanner(reader),  new PrintStream(writer));
		
		// Test
		assertEquals("u2 u1 t1 q2 q1\n", writer.remove());
		assertEquals("u1 t1 q2 q1\n", writer.remove());
		assertEquals("\n", writer.remove());
		assertEquals("q2\n", writer.remove());
		assertEquals("q2\n", writer.remove());
		assertEquals("\n", writer.remove());
		assertEquals("u1 t1 q2 q1\n", writer.remove());
		assertEquals("t1 u1\n", writer.remove());
		assertEquals("u1 t1\n", writer.remove());
	}
	
	@Test
	public void simpleQueryTest() throws IOException {
		reader.add("5");
		reader.add("ADD user u1 1.0 Adam D'Mero");
		reader.add("ADD user u2 1.0 Adam Black");
		reader.add("ADD user u3 2.0 John Doe");
		reader.add("QUERY 10 Adam");
		reader.add("QUERY 10 John");		
		TypeSuggest.run(new Scanner(reader), new PrintStream(writer));
		
		String test = writer.remove();
		assertEquals("u2 u1\n", test);
		
		test = writer.remove();
		assertEquals("u3\n", test);
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
