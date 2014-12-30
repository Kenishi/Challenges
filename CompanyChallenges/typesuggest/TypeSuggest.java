import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
 * Type Suggest Challenge
 * 
 * You are tasked with creating a program that can suggest results from
 * previously submitted data.
 * 
 * The program will receive inputs from the console/STDIN with actions.
 * 
 * There are 4 types of actions: ADD/DEL/QUERY/WQUERY
 * 
 * =====
 * 
 * When an ADD is performed you will be given a 1 of 4 data types:
 * USER, QUESTION, TOPIC, BOARD
 * 
 * Each add will have have an id, score, and data string associated with it.
 * ID will be in the form of u1, u2, q4, t3, etc.
 * Score will be a floating point number.
 * Strings will be printable ascii characters
 * 
 * For Example:
 * 
 * ADD user u1 10 John Doe
 * ADD board b1 50 Football
 * 
 * =====
 * 
 * When a DEL is performed you will be given the ID of the item to delete.
 * For Example: 
 * DEL u1
 * 
 * =====
 * 
 * When a QUERY is performed you will be given the number of expected results and 
 * a query string. For example:
 * QUERY 10 John
 * 
 * =====
 * 
 * When a WQUERY is performed you will be given the number of expected results, the
 * number of boosts, the boosts expected, and a query string.
 * For Example:
 * WQUERY 10 2 u1:3.0 topic:5.0 John
 * 
 * This query expects a max of 10 results. It has 2 boosts, the first is a boost on user
 * ID u1 for a value of 3.0, and a boost on all topics by 5.0, and "John" is our query string.
 * 
 * The boosts should be multiplied against a match's score cumulitevly.
 * 
 * =====
 * 
 * When a QUERY or WQUERY is performed. You need to search the current data and find all
 * data which match all of the query string values (case-insensitive). Partial matches 
 * from the front of a string are also ok. For example:
 * 
 * ADD question q1 30 "Where is atlanta?"
 * ADD question q2 20 "Where is atlantis?"
 * ADD questions q3 10 "What is atlantis?"
 * QUERY 10 where atlant
 * 
 * This query would match q1 and q2 because both contain 'where' and partially
 * match "atlant." q3 isn't a match because q3 doesn't match "where."
 * 
 * The results of a QUERY/WQUERY should be printed by the item ID and ordered in 
 * descending order of the score.
 * 
 * For Example:
 * 
 * ADD question q1 20 Where is atlanta?
 * ADD question q2 30 Where is atlantis?
 * ADD question q3 10 What is atlantis?
 * ADD board b1 10 Atlanta
 * ADD board b2 5 Atlantis
 * QUERY 10 where atlant
 * WQUERY 10 2 q1:10 board:100 atlant
 * 
 * Output:
 * q2 q1
 * b1 b2 q1 q2 q3 
 * 
 * The QUERY outputs q2 first because that score is 30; followed by q1 with a score of 20.
 * Remember, all searches are case-insensitive. 
 * The WQUERY outputs b1 and b2 first because all boards were boosted by 100 (b1: 10*100 b2: 5*100).
 * q1 is next because that specific ID was boosted by 10 making q1 now have a boosted score of 200.
 * q2 and q3 are matches but aren't boosted so they are last.
 * 
 * If a Query finds nothing, a blank line should be printed.
 * 
 * =====
 * 
 * Finally, at the start of the program, the number of events to be expected is printed and then
 * actions are fed in.
 * 
 * Sample case
 * ===========
 * 
 * Inputs:
 * 
 * 15
 * ADD user u1 1.0 Bob D’Mero
 * ADD user u2 1.0 Adam Black
 * ADD topic t1 0.8 Adam D’Mero
 * ADD question q1 0.5 What does Adam D’Mero do at Marvel?
 * ADD question q2 0.5 How did Adam D’Mero learn to draw?
 * QUERY 10 Adam
 * QUERY 10 Adam D’M
 * QUERY 10 Adam Cheever
 * QUERY 10 LEARN how
 * QUERY 1 lear H
 * QUERY 0 lea
 * WQUERY 10 0 Adam D’M
 * WQUERY 2 1 topic:9.99 Adam D’M
 * DEL u2
 * QUERY 2 Adam
 * 
 * Outputs:
 * u2 u1 t1 q2 q1
 * u1 t1 q2 q1
 * 
 * q2
 * q2
 *         
 * u1 t1 q2 q1
 * t1 u1
 * u1 t1
 * =========
 * Note the blank lines above. Those are queries that returned no results.
 * 
 * Constraints:
 * 0 < Event Count < 100000
 * 0 < ADD events < 40000
 * 0 < DEL events < 10000
 * 0 < QUERY events < 20000
 * 0 < WQUERY events < 1000
 * 0 < number of boosts < 25
 * 0.0 < score < 100.0
 * 0 < data string length < 100 chars
 * 
 */

/*
 * Type Suggest Solution
 * Author: Jeremy May
 * 
 * The solution to this challenge was inspired by a radix
 * trie layout. My design is simple, but it comes at a cost.
 * 
 * A radix trie tries to minimize the number of nodes by
 * grouping sections of a word together.
 * Example: 
 * Atlanta: (Atla)->(nta)
 * Atlas: (Atla) -> (s)
 * 
 * This is more memory efficient but it also means that as
 * the data size grows you will have to make more modifications
 * to the tree layout.
 * 
 * In the case of this challenge, that could mean splitting new
 * nodes off and moving data around. I felt this would probably
 * increase the running time significantly and opted for this simpler
 * solution. See below the description on the algorithm processes,
 * for a description of problems I recognize with my implementation.
 * 
 * The benefit of this setup, whether its my implementation or
 * an actual radix trie, is that you only have to run 1 search when
 * querying. Because every word in a Data object is stored (referentially)
 * at its place on the tree, you can know immediately if the other
 * Data objects at the same place will match in a query. You will also
 * know that all children below the target search node will contain part
 * of the search string.
 * 
 * For example:
 * QUERY: "Atla"
 * ROOT -> A -> T -> L -> A
 * 
 * Final A can have links to:
 * A-> S ("Atlas")
 * A-> N -> T -> A (Atlanta)
 * A-> N -> T -> I -> S (Atlantis)
 * 
 * So a query like "where is atla" can yield question data such as:
 * "where is atlanta?" or "where is atlantis?" or "where is my atlas?"
 *  
 * For a better understanding of the algorithm, read the ADD/QUERY steps
 * below.
 * 
 * ========================
 * 
 * A description on the ADD process
 *  
 * Example: "ADD user u1 1.0 John Doe"
 * ------------------------
 * 1) The string is parsed and verified for valid ACTION type(ADD),
 * 		score, and id.
 * 		NOTE: Checking for already existing IDs is not done now.
 * 2) The data string is split at the spaces.
 * 3) A data type containing the id, score, and string data; is made.
 * 4) An add() is called on the SearchTree and the Data object is passed
 * 		in.
 * 5) A check is made against a lookup table on IDs and if there is a match
 * 		the add is aborted and an UnexpectedStateException is thrown.
 * 6) If the id doesn't exist then the search-add begins.
 * 7) The first part of the split string data is taken and the algorithm
 * 		goes letter by letter working down the tree.
 * 
 * 		First it'll start with 'j' then move to the 'o' node then 'h' -> 'n'.
 * 8) Once the algorithm reaches the end of the string ("john") it will add
 * 		the entire Data object to an ArrayList there.
 * 9) The algorithm will then return to Step 7 taking the next data
 * 		string, "doe", and begin the process again.
 * 
 * ========================
 * 
 * A description on the QUERY process.
 * 
 * Example: "QUERY 10 Adam"
 * ------------------------
 * A QUERY happens almost like ADD
 * 
 * 1) The string is parsed and verified.
 * 2) The query string is sorted for the SMALLEST word first.
 * 3) The smallest word is passed into the query search along with all the other words.
 * 4) The algorithm moves node to node in the trie, character by character in the string, 
 * 5) Once the search reaches the end of the string and is on the node that matches the string;
 * 		for example, going 'a'->'d'->'a'->'m'
 * 		It begins gathering all the data from that node and the children below.
 * 6) Every Data object is compared against the entire full_terms using a RegEx and those
 * 		objects which match all the terms are added to the RESULTS.
 * 7) Once the end of the tree is reached, the recursion loop returns with the results.
 * 8) The results are sorted by score 
 * 9) The result IDs are printed.
 * 
 * A query only takes O(k) time to find 
 * 
 * ========================
 * 
 * A description on the WQUERY process.
 * 
 * 1-7) Same as QUERY, but Boosts are parsed from the string as well and turned into
 * 		a Boost object.
 * 8) The results are passed into a WeightedSort with the Boosts.
 * 9) The weighted sort checks each result and multiplies it by its boost.
 * 10) The results are sorted by score.
 * 11) The result IDs are printed.
 * 
 * ========================
 * 
 * Problems:
 * 
 * There are a number of problems with this implementation that I will detail now.
 * 
 * - Recursion stops at 120 nodes deep in order to avoid blowing the call stack.
 * 		This means that a single word in the string can not be over 120 characters long.
 * 
 * - Memory Footprint: 
 * 		As I detailed at the start. This is a much simplified version of a radix trie.
 * 		This means the longer a word, the more nodes that are created.
 * 		The nodes in general are kept small by only creating a new node when there is
 * 		a need. The node array is a HashMap while the storage of a Data object is
 * 		an ArrayList.
 * 
 * - Deletion:
 * 		Deleting an object is done using its ID. It would be time consuming to find the
 *		matching data object in the tree that goes with the ID. As a result, a lookup
 *		table is used to retrieve the Data object that goes with the ID. This causes
 *		a small hit on memory as the entries grow
 * 
 */

public class TypeSuggest {
	
	enum ACTION {
		ADD("add"),
		QUERY("query"),
		WQUERY("wquery"),
		DEL("del");
		
		private String m_action = null;
		
		private ACTION(String action) {
			m_action = action;
		}
		
		public static ACTION valueToAction(String val) {
			ACTION[] actions = ACTION.values();
			for(ACTION action : actions) {
				if(action.action().equals(val)) return action;
			}
			return null;
		}
		private String action() { return m_action; }
		
	}
	
	/**
	 * The tree where all items are stored
	 */
	private SearchTree m_tree = new SearchTree();
	
	public static void main(String[] args) throws IOException {
		run(new Scanner(System.in), System.out);
	}
	
	/**
	 * The main program function.
	 * 
	 * This exists so it is possible to easily test edge cases and functionality.
	 * 
	 * @param input The stream to read the inputs from.
	 * @param out The stream to print results/data out to.
	 * @throws IOException Thrown if there are problems writing/reading from streams.
	 */
	public static void run(Scanner input, PrintStream out) throws IOException {		
        TypeSuggest solution = new TypeSuggest();
        
		// Get events to expect
		String in = null;
		try {
			in = input.nextLine();
		} catch(NoSuchElementException e) {
			throw new IllegalQueryException("No event count found.");
		}
		
		int count;
		try {
			count = Integer.parseInt(in);
		} catch(NumberFormatException e) {
			throw new IllegalQueryException("Bad event number. Please input a proper number for the event count.");
		}
		
		// Begin processing
		for(int i = 0; i < count; i++) {
			try {	
				in = input.nextLine();
				solution.processLine(in, out);
			} catch(NoSuchElementException e) {
				break;
			}
		}
	}

	/**
	 * Process the input and do the action if applicable
	 * 
	 * @param line The line to process
	 * @param out The stream to write out to. This is generally System.out
	 * @throws IOException Thrown if there are problems writing to the 'out stream.
	 */
	private void processLine(String line, PrintStream out) throws IOException {
		if(line == null || line.length() <= 0) { throw new IllegalQueryException("Input line was null in process line."); }
		
		line = line.toLowerCase().trim();
		String[] split = line.split("\\p{Blank}");
		
		ACTION action = ACTION.valueToAction(split[0]);
		if(action == null) { throw new IllegalQueryException("Invalid action supplied: " + split[0]); }
		
		String results = null;
		switch(action) {
		case ADD:
			doAdd(split);
			break;
		case QUERY:
			results = doQuery(split);
			if(results == null) return;
			results += "\n";
			out.write(results.getBytes());
			out.flush();
			//System.out.println(results);
			break;
		case WQUERY:
			results = doWQuery(split);
			if(results == null) return;
			results += "\n";
			out.write(results.getBytes());
			out.flush();
			//System.out.println(results);
			break;
		case DEL:
			doDel(split);
			break;
		}
	}
	
	/**
	 * Perform a regular query
	 * @param input The string inputed from the console split at the spaces
	 * @return A string to be written back to the user
	 */
	private String doQuery(String[] input) {
		if(input == null) { throw new NullPointerException(); }
		if(input.length <= 2) { throw new IllegalQueryException("Query requires a data string"); }
		
		int numOfResult;
		try {
			numOfResult = Integer.valueOf(input[1]);
		} catch(NumberFormatException e) {
			throw new IllegalQueryException("Invalid format for number of results: " + input[1]);
		}
		if(numOfResult <= 0) { return ""; }
		
		String data = join(input, 2, input.length-1);
		return m_tree.query(numOfResult, data);
	}
	
	/**
	 * Perform the weight query.
	 * @param input The string inputed from the console split at the spaces
	 * @return A string to be written back to the user
	 */
	private String doWQuery(String[] input) {
		if(input == null) { throw new NullPointerException(); }
		if(input.length <= 3) { throw new IllegalQueryException("Not enough arguements for WQuery"); }
		
		int numOfResult;
		try {
			numOfResult = Integer.valueOf(input[1]);
		} catch(NumberFormatException e) {
			throw new IllegalQueryException("Invalid format for number of results: " + input[1]);
			//return null;
		}
		if(numOfResult <= 0) { return ""; }
		
		int numOfBoosts = -1;
		try {
			numOfBoosts = Integer.valueOf(input[2]);
		} catch(NumberFormatException e) {
			throw new IllegalQueryException("Invalid format for number of boosts: " + input[2]);
		}
		if(numOfBoosts > 0) {
			Boost[] boosts = new Boost[numOfBoosts];
			for(int i=1, bIndex=0; i <= numOfBoosts; i++, bIndex++) {
				Boost boost = createBoost(input[i+2]); // i+2 because boosts start from index 3
				if(boost == null) {
					throw new UnexpectedStateException("Error creating boost, check type: " + input[i+2]);
				}
				boosts[bIndex] = boost;
			}
			int dataStart = 3 + numOfBoosts;
			String data = join(input, dataStart, input.length-1);
			
			if(data.equals("")) return "";
			return m_tree.wquery(numOfResult, data, boosts);
		}
		else {
			String data = join(input, 3, input.length-1);
			if(data.equals("")) return "";
			return m_tree.query(numOfResult, data);
		}
	}
	
	/**
	 * Perform deletion of ID
	 * @param input The input string from the console split by spaces.
	 */
	private void doDel(String[] input) {
		if(input == null) { throw new NullPointerException(); }
		if(input.length != 2) { throw new IllegalQueryException("Delete requires an ID"); }
		
		String id = input[1];
		m_tree.delete(id);
	}
	
	/**
	 * Do the Add of the data supplied
	 * @param input The input string from the console split by spaces.
	 */
	private void doAdd(String[] input) {
		if(input == null) { throw new NullPointerException(); }
		if(input.length <= 4) { throw new UnexpectedStateException("Add requires a data string"); }		
		
		String addType = input[1];
		AbstractData data = null;
		if(addType.equals("user")) {
			data = createData(TYPE.USER, input[2], input[3], join(input, 4, input.length-1));
		}
		else if(addType.equals("topic")) {
			data = createData(TYPE.TOPIC, input[2], input[3], join(input, 4, input.length-1));
		}
		else if(addType.equals("question")) {
			data = createData(TYPE.QUESTION, input[2], input[3], join(input, 4, input.length-1));
		}
		else if(addType.equals("board")) {
			data = createData(TYPE.BOARD, input[2], input[3], join(input, 4, input.length-1));
		}
		else {
			throw new UnexpectedStateException("Unexpected add type: " + addType);
		}
		if(data != null) {
			m_tree.add(data);
		}
	}
	
	/**
	 * Create a single boost
	 * @param data A string holding a boost paramter. Ex: "topic:2.00"
	 * @return A boost object representing the data string
	 */
	private Boost createBoost(String data) {
		String[] split = data.split(":");
		if(split.length != 2) return null;
		double weight = 1;
		try {
			weight = Double.valueOf(split[1]);
		} catch(NumberFormatException e) {
			throw new IllegalQueryException("Error parsing boost score: " + split[1]);
		}
		
		// Check for data type
		String type = split[0];
		if(type.equals("topic")) {
			return new TypeBoost(TYPE.TOPIC, weight);
		}
		else if(type.equals("user")) {
			return new TypeBoost(TYPE.USER, weight);
		}
		else if(type.equals("question")) {
			return new TypeBoost(TYPE.QUESTION, weight);
		}
		else if(type.equals("board")) {
			return new TypeBoost(TYPE.BOARD, weight);
		}
		else { // Try id based Boost
			TYPE charType = TYPE.valueToType(Character.toString(split[0].charAt(0)));
			if(charType == null) { return null; }
			
			int id = -1;
			try {
				id = Integer.valueOf(split[1].substring(1));
			} catch(NumberFormatException e) {
				throw new IllegalQueryException("Error parsing id in wquery: " + split[1]);
			}
			return new IdBoost(charType, weight, id);
		}
	}
	
	/**
	 * Create a data type
	 * 
	 * @param type The type of the data
	 * @param idStr A string with the id. Ex Format: "u21", "t3", "b8", etc...
	 * @param scoreStr The data's score as a string double
	 * @param data A string with the data
	 * @return Return an AbstractData if successful creating, null if there were errors.
	 */
	private AbstractData createData(TYPE type, String idStr, String scoreStr, String data) {
		// Parse ID to int
		int id;
		try {
			id = Integer.valueOf(idStr.substring(1));
		}
		catch(NumberFormatException e) {
			throw new IllegalQueryException("Error parsing id: " + idStr);
		}
		
		// Parse SCORE to double
		double score;
		try {
			score = Double.valueOf(scoreStr);
		}
		catch(NumberFormatException e) {
			throw new IllegalQueryException("Error parsing score: " + scoreStr);
		}
		
		// Create data type and return
		switch(type) {
			case USER:
				return new UserData(id, score, data);
			case TOPIC:
				return new TopicData(id, score, data);
			case QUESTION:
				return new QuestionData(id, score, data);
			case BOARD:
				return new BoardData(id, score, data);
			default:
				throw new IllegalQueryException("Unexpected unknown data type");
		}
	}
	
	/**
	 * Join string array back into a string based on start/stop index using
	 * 	spaces as deliminter
	 * 
	 * @param data String array to join
	 * @param start Start index in array (inclusive)
	 * @param stop Stop index in array (inclusive)
	 * @return A string with the array joined together
	 */
	private String join(String[] data, int start, int stop) {
		if(start < 0 || stop > data.length || start > stop) throw new IndexOutOfBoundsException();
		
		String out = "";
		for(int i = start; i <= stop; i++) {
			out += data[i] + " ";
		}
		return out.trim();
	}
	
	/* package typeahead.tree.* */
	
	class SearchTree extends Node {
		private HashMap<String, AbstractData> lookupTable = new HashMap<String, AbstractData>();
		
		public SearchTree() {
			super();
		}
		/**
		 * Add a data object to the tree
		 * @param data The data object to add
		 * @return Return true if it was successful, false if the RECURSE_LIMIT was reached.
		 */
		public boolean add(final AbstractData data) {
			boolean success = true;
			if(lookupTable.get(data.getID()) != null) {
				throw new UnexpectedStateException("Overwriting IDs is not allowed. Please delete old id first.");
			}
			for(int dIndex = 0; dIndex < data.data().length && success; dIndex++) {
				String search = data.data()[dIndex];
				success = this.add(data, search, 0);
				if(!success) {
					throw new UnexpectedStateException("Recurse limit reached during add on term: " + search);
				}
			}
			if(success) {
				lookupTable.put(data.getID(), data);
			}
			
			return success;
		}
		
		/**
		 * Delete a data using its ID
		 * @param id An id to a Data to delete
		 * @return Returns True if delete was successful, False if it wasn't found or RECURSE_LIMIT was hit 
		 */
		public boolean delete(String id) {
			AbstractData data = lookupTable.get(id);
			if(data == null) { return false; }
			else {
				boolean success = delete(data);
				if(success) { lookupTable.remove(id); }
				return success;
			}
		}
		
		/**
		 * Delete using a data object
		 * @param data The data object to delete from the tree
		 * @return Returns True if delete was successful, False if it wasn't found or RECURSE_LIMIT was hit
		 */
		public boolean delete(final AbstractData data) {
			boolean success = true;
			for(int dIndex = 0; dIndex < data.data().length && success; dIndex++) {
				String search = data.data()[dIndex];
				success = this.delete(data, search, 0);
			}
			return success;
		}
		
		public String query(final int numOfResult, final String query) {
			if(numOfResult <= 0) { return ""; }
			else if(query == null || query.trim().equals("")) { return ""; }
			
			// Search
			String[] full_terms = query.trim().split("\\p{Blank}");
			Arrays.sort(full_terms); // Sort small to large to speed up search
			HashMap<String, AbstractData> results = null;
			results = search(full_terms);
			
			// Sort
			AbstractData[] dataArray = new AbstractData[results.values().size()];
			results.values().toArray(dataArray);
			Arrays.sort(dataArray, 0, dataArray.length, Collections.reverseOrder());
			
			// Build String
			String output = "";
			int remaining = numOfResult;
			for(AbstractData data : dataArray) {
				if(data != null) {
					output += data.getID() + " ";
					--remaining;
					if(remaining <= 0) break;
				}
			}
			output = output.trim();
			return output;
		}
		
		public String wquery(final int numOfResult, final String query, Boost[] boosts) {
			if(numOfResult <= 0) { return ""; }
			else if(query == null || query.trim().equals("")) { return ""; }
			
			// Search
			String[] full_terms = query.trim().split("\\p{Blank}");
			Arrays.sort(full_terms); // Sort small to large to speed up search
			HashMap<String, AbstractData> results = null;
			results = this.search(full_terms);
			
			// Sort
			WeightedSort wSort = new WeightedSort(boosts);
			AbstractData[] dataArray = new AbstractData[results.values().size()];
			results.values().toArray(dataArray);
			dataArray = wSort.sort(dataArray);
			
			// Build String
			String output = "";
			int remaining = numOfResult;
			for(AbstractData data : dataArray) {
				if(data != null) {
					output += data.getID() + " ";
					--remaining;
					if(remaining <= 0) break;
				}
			}
			output = output.trim();
			return output;
		}
		
		/**
		 * Search the tree for the query results
		 * @param full_terms A string array holding all the query terms. Should be lowerCase alredy.
		 * @return Return a HashMap holding the results of the search. 
		 */
		private HashMap<String, AbstractData> search(final String[] full_terms) {
			HashMap<String, AbstractData> results = new HashMap<String, AbstractData>();
			
			results = this.search(full_terms[0], 0, results, full_terms);
			return results;
		}
	}
	
	/**
	 * 
	 * Class used for weighted queries.
	 * 
	 * Feed in the boosts and then perform sort()
	 *
	 */
	class WeightedSort {
		private Boost[] m_boosts = null;
		
		/**
		 * Constructor 
		 * 
		 * @param boosts An array of boosts to be used during the sort
		 */
		public WeightedSort(Boost[] boosts) {
			m_boosts = boosts;
		}
		
		/**
		 * Sort array of data in descending order, applying the boosts from
		 * the constructor.
		 * 
		 * @param data An array of data to be sorted.
		 * @return Return an array sorted in descending order
		 */
		public AbstractData[] sort(AbstractData[] data) {
			AbstractData[] boostedData = addBoosts(data);
			Arrays.sort(boostedData, Collections.reverseOrder());
			return boostedData;
		}
		
		/**
		 * Multiply up the boosts for each matching data.
		 * 
		 * @param copy An array of the data. This function will change the score on each matching data.
		 * @return Return the inputed array with updated scores.
		 */
		private AbstractData[] addBoosts(AbstractData[] data) {
			// Clone the array
			AbstractData[] copy = new AbstractData[data.length];
			for(int index = 0; index < data.length; index++) {
				copy[index] = data[index].clone();
			}
			
			// Check Boosts against results and multiply boosts
			for(int bIndex = 0; bIndex < m_boosts.length; bIndex++) {
				for(int dIndex = 0; dIndex < copy.length; dIndex++) {
					if(m_boosts[bIndex] instanceof IdBoost) {
						// Check if Data's ID (ex: 'u21') equals the Boost ID 
						boolean isIdMatch = copy[dIndex].getID().equals(((IdBoost)m_boosts[bIndex]).idStr()); 
						if(isIdMatch) {
							copy[dIndex].m_score *= m_boosts[bIndex].boost();
						}
					}
					else {
						boolean isTypeMatch = copy[dIndex].type() == ((TypeBoost)m_boosts[bIndex]).type();
						if(isTypeMatch) {
							copy[dIndex].m_score *= m_boosts[bIndex].boost();
						}
					}
				}
			}
			
			return copy;
		}
	}
	
	/*******************
	 * 
	 * Boost Types
	 *
	 *******************/
	abstract class Boost {
		public double m_boost = 0.00;
		
		public Boost(double boost) {
			m_boost = boost;
		}
		public double boost() { return m_boost; }
	}
	
	class TypeBoost extends Boost{
		private TYPE m_type = null;
		public TypeBoost(TYPE type, double boost) {
			super(boost);
			m_type = type;
		}
		public TYPE type() { return m_type; }
	}
	
	class IdBoost extends TypeBoost {
		private int m_id = -1;
		public IdBoost(TYPE type, double boost, int id) {
			super(type, boost);
			m_id = id;
		}
		public int id() { return m_id; }
		public String idStr() { return type().tag() + Integer.toString(m_id); }
	}
	
	/*****************************
	 * 
	 * Node Class for the Search Tree
	 * 
	 * Description: 
	 * 		Each node contains an array of node branches, size 68.
	 * 		If a a branch is null, then we know that there are Data
	 * 			down that tree.
	 * 		
	 * 		Each node also contains a dynamic array that can store Data.
	 * 		Finally, each node has a "name" which holds the partial text
	 * 			up to that that.
	 * 		
	 * 		Example:
	 * 			Node foo = new Node("foo");
	 * 			System.out.println(foo.name());  // "foo"
	 * 			// If we add a data item with text "foobar" (not shown here) then..
	 * 			String nextNodeName = foo.m_branches[convertToArrayIndex('b')].name();
	 * 			System.out.println(nextNodeName); // "foob"
	 *
	 *****************************/
	class Node {
		private static final short RECURSE_LIMIT = 120;
		//private Node[] m_branches = new Node[68];
		private HashMap<Character, Node> m_branches = new HashMap<Character, Node>(); 
		private ArrayList<AbstractData> m_dataArray = new ArrayList<AbstractData>();
		private String m_name = null;
		
		public Node(String name) {
			if(name == null || name.trim().equals("")) { throw new IllegalQueryException("Node name cannot be null."); }
			m_name = name;
		}
		
		/**
		 * Constructor for Root Node only
		 */
		protected Node() {
			m_name = "";
		}
		
		/**
		 * Search the current branch.
		 * 
		 * Search works by first moving all the way to the end of the Tree where
		 * 	"search == node.name"  and then begins to gather all the Data below
		 * 	this level.
		 * 
		 * @param search A string which is part of the full_terms to search for
		 * @param loc Current character in the search string 
		 * @param results A hashmap with all the data collected so far
		 * @param full_terms A string array holding the full query terms
		 * @return Return a HashMap with all the data that matched all terms
		 */
		public HashMap<String, AbstractData> search(final String search, final int loc,
				HashMap<String, AbstractData> results, final String[] full_terms) {
			if(search == null || results == null || search.trim().equals("")) { return results; }
			
			// Check if we have reached the end of the search string
			//	and can start start gathering Data
			if(loc < search.length()) { // Not yet
				Node node = m_branches.get(search.charAt(loc));
				if(node == null) { return results; }
				
				int newLoc = loc + 1;
				return node.search(search, newLoc, results, full_terms);
			}
			else if(loc > search.length()) { // Unexpected case
				throw new UnexpectedStateException("Search location exceeds search string length.");
			}
			
			return gatherSearchResults(results, full_terms);
		}
		
		/**
		 * Try adding data to node.
		 * 
		 * If the data doesn't belong here, add to next level
		 * till it finds a home, creating nodes along the way.
		 * 
		 * NOTE: This call is RECURSIVE and will abort at loc >= RECURSE_LIMIT.
		 * 
		 * @param data The data to add
		 * @param search The search string compared against the node name. Search must be lower-case.
		 * @param loc The character index in the string we are looking for.
		 * @return True if add was a success, False if it hit the RECURSE_LIMIT
		 */
		protected boolean add(final AbstractData data, final String search, final int loc) {
			// Verify parameters
			if(search == null || search.trim().equals("")) { throw new IllegalQueryException("ADD: No search parameter"); }
			if(loc < 0 || loc > search.length()) { throw new IndexOutOfBoundsException(); }
			
			// Check if search is finished, and add to data if it is
			if(loc == search.length()) {
				m_dataArray.add(data);
				return true;
			}
			
			Node branch = m_branches.get(search.charAt(loc));
			// If the next node doesn't exist create one and continue add()
			if(branch == null) {
				branch = addNode(search.charAt(loc));
			}
			
			// Recurse add, abort if loc is >= 120.
			int newLoc = loc + 1;
			if(newLoc >= Node.RECURSE_LIMIT) { return false; }
			else { return branch.add(data, search, newLoc); }
		}
		
		/**
		 * Delete the Data from this branch 
		 * 
		 * @param data The data to delete
		 * @param search A string representing the final place on the tree
		 * @param loc An int holding current spot in the search string
		 * @return True if the delete was successful, False if it wasn't due to RECURSE_LIMIT or not deleting
		 */
		protected boolean delete(final AbstractData data, final String search, final int loc) {
			// Verify parameters
			if(search == null || search.trim().equals("")) { throw new IllegalQueryException("DELETE: No search parameter."); }
			if(loc < 0 || loc > search.length()) { throw new IndexOutOfBoundsException(); }
			
			// Check if search is finished, and add to data if it is
			if(loc == search.length()) {
				return m_dataArray.remove(data);
			}
			
			Node branch = m_branches.get(search.charAt(loc));
			// If the next node doesn't exist create one and continue add()
			if(branch == null) {
				return false;
			}
			
			// Recurse add, abort if loc is >= 120.
			int newLoc = loc + 1;
			if(newLoc >= Node.RECURSE_LIMIT) { return false; }
			else { return branch.delete(data, search, newLoc); }			
		}
		
		/**
		 * Add node to branch
		 * @param ch The character on the array
		 */
		private Node addNode(final char ch) {
			if(m_branches.get(ch) != null) { throw new UnexpectedStateException("Overwrite attempt on existing node"); }
			
			String name = m_name + ch;
			Node node = new Node(name);
			m_branches.put(ch, node);
			return node;
		}
		
		/**
		 * Gather data from this node and children, which match the search terms.
		 * 
		 * @param results A HashMap which stores the current matched results
		 * @param full_terms A string array holding the full set of search terms
		 * @return Return the results HashMap passed in with any new matching Data added to the map
		 */
		private HashMap<String, AbstractData> gatherSearchResults(HashMap<String, AbstractData> results, final String[] full_terms) {
			// Check if current node has any matches
			for(AbstractData ele : m_dataArray) {
				// Don't process Data already in results
				if(results.containsKey(ele.getID())) continue;
				
				String[] data = ele.data();
				
				// Make sure search terms intersect data terms
				boolean isMatch = true;
				for(int fIndex = 0; fIndex < full_terms.length && isMatch; fIndex++) {
					for(int dIndex = 0; dIndex < data.length; dIndex++) {
						String dataTerm = data[dIndex];
						if(dataTerm.startsWith(full_terms[fIndex])) {
							isMatch = true;
							break;
						}
						isMatch = false;
					}
				}
				if(isMatch) {
					results.put(ele.getID(), ele);
				}
			}
			
			// Gather results from children
			for(Node node : m_branches.values()) {
				if(node != null) {
					results = node.gatherSearchResults(results, full_terms);
				}
			}
			
			return results;
		}
	}
	
	/* package typeahead.tree.data* */
	
	/**
	 * Enum for different data types
	 * 
	 * The enum also handles returning the correct prefix tag
	 * on an ID.
	 */
	enum TYPE {
		USER("u"),
		TOPIC("t"),
		QUESTION("q"),
		BOARD("b");
		
		private String m_type = null;
		TYPE(String ch) {
			m_type = ch;
		}
		public String tag() { return m_type; }
		public static TYPE valueToType(String val) {
			TYPE[] types = TYPE.values();
			for(TYPE type : types) {
				if(type.tag().equals(val)) return type;
			}
			return null;
		}
	}
	
	/**
	 * 
	 * Data classes
	 *
	 * Inheriting data types should have a factory method
	 * to create the instance and increment the ID of that
	 * sub-type.
	 */
	abstract static class AbstractData implements Comparable<AbstractData>, Cloneable {		
		private static int m_creationAcumulator = 0;

		/*
		 * The creationId is set through an accumulator.
		 * It is used to solve issues where scores will match
		 * 	and we need to know which data is newer.
		 */
		private int m_creationId = -1; 
		private TYPE m_type = null;
		private int m_id = -1;

		private double m_score = -1.0;
		private String[] m_data = null;
		

		abstract protected AbstractData clone();

		
		protected AbstractData(final TYPE type, final int id, final double score,
				final String data) {
			
			m_creationId = ++m_creationAcumulator;
			m_type = type;
			m_id = id;
			m_score = score;
			
			// Split the array and sort it to make 
			String[] dataArray = data.toLowerCase().split("\\p{Blank}");
			Arrays.sort(dataArray);
			m_data = dataArray;
		}
		
		/* Cloning Constructor */
		protected AbstractData(AbstractData obj) {
			this.m_creationId = obj.m_creationId;
			this.m_type = obj.m_type;
			this.m_id = obj.m_id;
			this.m_score = obj.m_score;
			this.m_data = obj.m_data;
		}
		
		public double score() { return m_score; }
		public String[] data() { return m_data; }
		
		public String getID() {
			String id_str = null;
			try {
				id_str = Integer.toString(m_id);
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
			
			return m_type.tag() + id_str;
		}
		
		@Override
		public int compareTo(AbstractData o) {
			if(this == o) return 0; // In case we compare against our self
			
			if(score() < o.score()) { return -1; }
			else if(score() > o.score()) { return 1; }
			else { // Scores equal, check which is newer
				if(this.m_creationId < o.m_creationId) { return -1; }
				else if(this.m_creationId > o.m_creationId) { return 1; }
				else {
					throw new UnexpectedStateException("Unexpected state. Creation IDs cannot be equal");
				}
			}
		}
		
		protected TYPE type() { return m_type; }
	}
	
	public static class UserData extends AbstractData {
		private static int m_idAccumulator = 0;
		
		protected UserData(int id, double score, String data) {
			super(TYPE.USER, id, score, data);
		}
		
		/* Cloning Constructor */
		private UserData(UserData c) {
			super(c);
		}
		
		@Override
		protected UserData clone() {
			return new UserData(this);
		}
		
		public static UserData createUser(double score, String data) {
			int id = increaseUsers();
			return new UserData(id, score, data);
		}
		
		private static int increaseUsers() { return ++m_idAccumulator; }
	}
	
	public static class TopicData extends AbstractData {
		private static int m_idAccumulator = 0;
		
		protected TopicData(int id, double score, String data) {
			super(TYPE.TOPIC, id, score, data);
		}
		
		/* Cloning Constructor */
		private TopicData(TopicData c) {
			super(c);
		}
		
		@Override
		protected TopicData clone() {
			return new TopicData(this);
		}
		
		public static TopicData createTopic(double score, String data) {
			int id = increaseTopics();
			return new TopicData(id, score, data);
		}
		
		private static int increaseTopics() { return ++m_idAccumulator; }
	}
	
	public static class QuestionData extends AbstractData {
		private static int m_idAccumulator = 0;
		
		protected QuestionData(int id, double score, String data) {
			super(TYPE.QUESTION, id, score, data);
		}
		
		private QuestionData(QuestionData c) {
			super(c);
		}
		
		@Override
		protected QuestionData clone() {
			return new QuestionData(this);
		}
		
		public static QuestionData createQuestion(double score, String data) {
			int id = increaseQuestions();
			return new QuestionData(id, score, data);
		}
		
		private static int increaseQuestions() { return ++m_idAccumulator; }
	}
	
	public static class BoardData extends AbstractData {
		private static int m_idAccumulator = 0;
		
		protected BoardData(int id, double score, String data) {
			super(TYPE.BOARD, id, score, data);
		}
		
		/* Cloning Constructor */
		private BoardData(BoardData c) {
			super(c);
		}
		
		@Override
		protected BoardData clone() {
			return new BoardData(this);
		}
		
		public static BoardData createBoard(double score, String data) {
			int id = increaseBoards();
			return new BoardData(id, score, data);
		}
		
		private static int increaseBoards() { return ++m_idAccumulator; }
	}
	
	@SuppressWarnings("serial")
	public static class IllegalQueryException extends RuntimeException {
		public IllegalQueryException(String str) {
			super(str);
		}
	}
	
	@SuppressWarnings("serial")
	public static class UnexpectedStateException extends RuntimeException {
		public UnexpectedStateException(String str) {
			super(str);
		}
	}
}
