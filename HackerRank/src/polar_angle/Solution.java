package polar_angle;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/*
 * Polar Angles
 * 
 * Passes all tests
 * 
 */

public class Solution {

	class Case implements Comparable<Case> {
		private double angle;
		private double x;
		private double y;
		
		public Case(double x, double y) {
			this.x = x;
			this.y = y;
			
			double rads = Math.atan2(y, x);
			angle = Math.toDegrees(rads);
			
			/*
			 * When in Quad 3 and 4, atan will calculate the angle
			 * going clockwise, so we substract from 180 to get 
			 * back to counter-clockwise/polar angle and add 180
			 * to get what was lost. 
			 */
			angle = angle < 0 ? ((180-Math.abs(angle)) + 180) : angle;
		}
		
		private double distFromOrigin() {
			double i = (x*x + y*y);
			return Math.sqrt(i);
		}

		@Override
		public int compareTo(Case o) {
			if(this.angle > o.angle) { return 1; }
			else if(this.angle < o.angle) { return -1; }
			else {
				if(this.distFromOrigin() > o.distFromOrigin()) { return 1; }
				else if(this.distFromOrigin() < o.distFromOrigin()) { return -1; }
				else { return 0; }
			}
		}	
		
		public String toString() {
			return (int)x + " " + (int)y;
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int cases = Integer.valueOf(line);
		
		ArrayList<Case> set = new ArrayList<>();
		reader.useDelimiter(" ");
		
		double x, y;
		Case c;
		String[] params;
		for(int i=0; i < cases; i++) {
			line = reader.nextLine();
			params = line.split(" ");
			x = Double.valueOf(params[0]);
			y = Double.valueOf(params[1]);
			c = new Case(x,y);
			set.add(c);
		}
		
		Collections.sort(set);
		
		// Print ordered
		String out;
		for(Case val : set) {
			out = val.toString() + "\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
}
