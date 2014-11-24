package count_luck;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Solution {

	public static void main(String[] args) throws IOException {
		new Solution().run(new Scanner(System.in), System.out);
	}
	
	public void run(Scanner reader, PrintStream writer) throws IOException {
		String line = reader.nextLine();
		int cases = Integer.valueOf(line);
		
		for(int i=0; i < cases; i++) {
			String[] params = reader.nextLine().split(" ");
			int n = Integer.valueOf(params[0]);
			
			String[] maze = new String[n];
			Point start=null, stop=null;
			for(int row=0; row < n; row++) {
				line = reader.nextLine();
				start = (start == null) && line.contains("M") ? new Point(line.indexOf("M"), row) : start;
				stop = (stop == null) && line.contains("*") ? new Point(line.indexOf("*"), row) : stop;
				maze[row] = line;
			}
			
			int k = Integer.valueOf(reader.nextLine());

			String out = isPossible(maze, k, start, stop) ? "Impressed\n" : "Oops!\n";
			writer.write(out.getBytes());
			writer.flush();
		}
	}
	
	private boolean isPossible(String[] maze, int k, Point start, Point stop) {
		// Add first set of moves to stack
		k = useWand(start, maze, null) ? k-1 : k;
		Move[] moves = movesPossible(start, maze, null, k);
		Stack<Move> moveStack = new Stack<>();
		for(Move m : moves) {
			moveStack.push(m);
		}
		
		boolean exitFound = false;
		while(!moveStack.empty() && !exitFound) {
			Move to = moveStack.pop();
			//System.err.println(to.toString());
			exitFound = move(to, maze, moveStack);
		}
		
		return exitFound;
	}
	
	private boolean useWand(Point at, String[] maze, Direction from) {
		int dir = 0;
		dir = from != Direction.UP && (at.y-1) >= 0 && (maze[at.y-1].charAt(at.x) == '*' || maze[at.y-1].charAt(at.x) == '.') ? dir + 1 : dir;
		dir = from != Direction.DOWN && (at.y+1) < maze.length &&
				(maze[at.y+1].charAt(at.x) == '*' || maze[at.y+1].charAt(at.x) == '.') ? dir + 1 : dir;
		dir = from != Direction.LEFT && (at.x-1) >= 0 && (maze[at.y].charAt(at.x-1) == '*' || maze[at.y].charAt(at.x-1) == '.') ? dir + 1 : dir;
		dir = from != Direction.RIGHT &&
				(at.x+1) < maze[at.y].length() &&
				(maze[at.y].charAt(at.x+1) == '*' || maze[at.y].charAt(at.x+1) == '.') ? dir + 1 : dir;
		return (dir > 1);
	}
	
	private boolean move(Move to, String[] maze, Stack<Move> stack) {
		boolean success = false;
		int k = to.k;
		// Check for exit at current place
		if(maze[to.spot.y].charAt(to.spot.x) == '*') {
			success = to.k == 0;
		}
		else {
			k = useWand(to.spot, maze, to.cameFrom) ? k-1 : k;
			Move[] moves = movesPossible(to.spot, maze, to.cameFrom, k);
			for(Move m : moves) {
				stack.push(m);
			}
		}
		return success;
	}
	
	private Move[] movesPossible(Point at, String[] maze, Direction ignore, int k) {
		ArrayList<Move> moves = new ArrayList<>();
		
		for(Direction d : Direction.values())  {
			int newY = at.y + d.y;
			int newX = at.x + d.x;
			if(ignore != d && // Don't go in direction we came from
					// Check bounds
					newY >= 0 &&
					newY < maze.length &&
					newX >=0 &&
					newX < maze[newY].length()) {
				if(maze[newY].charAt(newX) != 'X') {
					moves.add(new Move(new Point(newX, newY), d.cameFrom(), k));
				}
			}
		}

		Move[] out = new Move[moves.size()];
		moves.toArray(out);
		return out;
	}
	
	class Point {
		public int x, y;
		
		public Point(int x, int y) { 
			this.x=x;
			this.y=y;
		}
	}
	
	class Move {
		Point spot;
		Direction cameFrom;
		int k;
		public Move(Point s, Direction c, int k) {
			spot = s;
			cameFrom = c;
			this.k = k;
		}
		public String toString() {
			return "Move to: (" + this.spot.x + "," + this.spot.y + ")" + " FROM " + cameFrom.toString(); 
		}
	}
		
	enum Direction {
		UP(0,-1),
		DOWN(0, 1),
		LEFT(-1,0),
		RIGHT(1, 0);
		
		public int x, y;
		
		public Direction cameFrom() {
			if(this == DOWN) { return UP; }
			else if(this == UP) { return DOWN; }
			else if(this == LEFT) { return RIGHT; }
			else if(this == RIGHT) { return LEFT; }
			return null;
		}
		private Direction(int x, int y) { 
			this.x = x;
			this.y = y;
		}
	}
}
