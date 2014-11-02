package cavity_map;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int demi = Integer.valueOf(line);
		int[][] map = new int[demi][demi];
		
		// Get the map
		for(int x=0; x < demi; x++) {
			line = reader.nextLine();
			for(int y=0; y < demi; y++) {
				map[x][y] = Integer.valueOf(Character.toString(line.charAt(y)));
			}
		}
		
		map = findPits(map);
		
		// Print
		String out = "";
		for(int x=0; x < demi; x++) {
			out = "";
			for(int y=0; y < demi; y++) {
				out += map[x][y] < 0 ? "X" : Integer.toString(map[x][y]);
			}
			out += "\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	public int[][] findPits(final int[][] map) {
		int demi = map.length;
		int[][] pitMap = new int[demi][demi];
		pitMap = copyBorder(pitMap, map);
		
		boolean isPit = false;
		for(int x=1; x < demi-1; x++) {
			for(int y=1; y < demi-1; y++) {
				
				isPit = false;
				int cell = map[x][y];
				// Check up
				isPit = map[x][y-1] < cell;
				if(!isPit) {
					pitMap[x][y] = cell;
					continue;
				}
				
				// Check down
				isPit = map[x][y+1] < cell;
				if(!isPit) {
					pitMap[x][y] = cell;
					continue;
				}
				
				// Check left
				isPit = map[x-1][y] < cell;
				if(!isPit) {
					pitMap[x][y] = cell;
					continue;
				}
				
				// Check right
				isPit = map[x+1][y] < cell;
				pitMap[x][y] = isPit ? -1 : cell;
			}
		}
		
		return pitMap;
	}
	
	private int[][] copyBorder(int[][] pitMap, int[][] map) {
		int demi = map.length;
		// Top & Bottom
		for(int x=0; x < demi; x++) {
			pitMap[x][0] = map[x][0];
			pitMap[x][demi-1] = map[x][demi-1];
		}
		
		// Left & Right
		for(int y=0; y < demi; y++) {
			pitMap[0][y] = map[0][y];
			pitMap[demi-1][y] = map[demi-1][y];
		}
		return pitMap;
	}
}
