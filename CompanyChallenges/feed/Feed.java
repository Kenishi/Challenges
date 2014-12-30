import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

/*
 * Feed Challenge
 * 
 * Overview:
 * 
 * You are tasked with creating a news feed for a site. The news feed will
 * receive simple story inputs at various times along with refresh requests.
 * Every story will have a score, the time it was added/created, and the height
 * of the story in pixels.
 * 
 * At the start of the program you are given 3 variables:
 * Event count, Time window, Browser Height
 * 
 * The first line of input will have this information. For example:
 * 9 10 100
 * 
 * There will be 9 events after this first line. Your time window is 10, so stories
 * outside a time of 10 should not be considered at the time of refresh (example below).
 * You height window is 100.
 * 
 * =====
 * 
 * When you receive a story input (via console STDIN) it will come in the following format:
 * 
 * S 10 50 35
 * 
 * 'S' for story. 10 is the time. 50 is the score. 35 is the height.
 * 
 * In addition, every story will have an ID which increases with each new addition.
 * You, the developer, will need to keep track of that id.
 * 
 * IDs start from 1.
 * 
 * ======
 * 
 * When you receive a refresh from input it will come in the following format:
 * 
 * R 20
 * 
 * 'R' for refresh. 20 is the time of the refresh.
 * 
 * On refresh, Your goal is to pick the stories that yield the maximum score without going 
 * over the max height of the window.
 * If multiple sets yield the same score, choose the one with fewer stories.
 * If there are sets with the same score and same number of stories, then choose the set which
 * is lexographically smaller based on ids. For example, { 1, 2, 4 } is smaller than { 1, 3, 4 }.
 * 
 * ======
 * 
 * When refresh is called you should print out (via STDOUT) your selection in the following format:
 * (Total Score) (Story Count) (ID) (ID) (ID) ....
 * 
 * For example:
 * 
 * 150 3 1 3 4
 * 
 * 150 is total score of the set. 3 is how many stories there are. 1 3 4 are the story ids.
 * 
 * You should output IDs in increasing order of their ID.
 * 
 * ======
 * 
 * Sample Input
 * 
 * 9 10 100
 * S 11 50 30
 * R 12
 * S 13 40 20
 * S 14 45 40
 * R 15
 * R 16 
 * S 18 45 20
 * R 21
 * R 22
 * 
 * Sample Ouput
 * 
 * 50 1 1
 * 135 3 1 2 3
 * 135 3 1 2 3
 * 140 3 1 3 4
 * 130 3 2 3 4   (Note: Story 1 is excluded here because story 1 is now > 10 time units old)
 *
 * Constraints
 * Times: 1 to 1,000,000,000
 * Scores: 1 to 1,000,000
 * Heights: All positive integers
 * Event count: 0 < N <= 10,000
 * Window Height: 0 < H <= 2,000
 * Time Window: 0 < W <= 2,000
 */

/*
 * Feed Challenge Solution
 * 
 * Author: Jeremy May
 * Check my resume out at: http://aberdynic.appspot.com/?id=fullres-81045
 * 
 * The feed challenge is basically a modified Knapsack problem.
 * 
 * I opted to solve this problem using the Branch-and-Bound search method.
 * 
 * The flow of the program is such:
 * 
 * 1) Feed main calls run() w/ STDIN & STDOUT.
 * 
 * 2) run() will create a SortedDensityArray instance which stores Story objects
 * 	with high score to height ratios first.
 * 
 * 3) run() will also create a SetSearchTree instance which is the branch-and-bound
 * 	search tree used to find the best possible story set for the feed.
 * 
 * 4) When a refresh is requested, run() will call refresh on the Search Tree.
 * 	The search tree will prune any stories outside the window and then begin the
 * 	search.
 *  
 *  Each node on the tree represents a decision of the stories to choose for the feed.
 *  
 *  Nodes do not hold actual Story data. Instead they hold an instance of class Selection,
 *  which is a wrapper for the BitSet class. 
 *  
 *  The class only expands the BitSet as far as is needed to represent the current selection
 *  of stories from the Density Array, and allows for multiple trailing bits to be set 0. 
 *  
 *  In other words, the class tracks the number of selections, something the default BitSet can't do
 *  if your selection was say "01000" (Don't choose 1st, 3rd, 4th, or 5th. Choose 2nd story).
 *  
 *  This decision keeps the memory requirements of the entire search tree low, as we can represent
 *  a selection decision of 64 stories in just 64-bits. The reason for this is because each node 
 *  holds a cloned (deep-copy) version of the  parent node's selection with additional new selection decision.
 *  
 * 5) The Tree goes best case first, assuming the nodes with the highest density are likely
 * 	to be the nodes that will yield the best set.
 * 
 * 6) Due to the likely hood that a search COULD go very deep, the algorithm traverses the tree
 * 	iteratively and uses a special search stack to do so.
 * 
 * 7) Each new possible node that can be moved to is added to the search stack. The search stack
 * 	will SORT the nodes so that the node with the best score or best theoretical score is on top of
 * 	the stack. In this way, we avoid searching down paths that may be less than optimal and decrease
 * 	search time.
 * 
 * 8) Each time the search tree fills up the height of the window with a set, that set/node is added
 * 	to a Result Set. The result set will keep track of the current best score and smallest selection set.
 * 
 * 9) If a new node is added to the result set which BEATS the prior score, or matches the score with fewer
 * 	stories; then the result set is discarded, that node/set is kept, and the score and count are updated.
 * 
 * 9.2) If a new node matches the count and score on the set, then that is added to the set. The Result set
 * 	doesn't have access to the actual density array so it isn't possible to cross check story id's with which
 * 	stories are selected. As a result, a lexicographical comparison can't be done till AFTER the tree search
 * 	has completed.  
 * 	 
 * 10) The search completes when either result set's score is greater than the max theoretical score OR
 * 	when the searchStack size reaches 0. 
 */

public class Feed {
	
	/**
	 * The accumulator for stories.
	 * <p>
	 * This is global due to the need for
	 * the accumulator to be reset for each
	 * new instance of the feed class.
	 * If it it is static and in Story, JUnit
	 * tests will share the accumulator across
	 * tests and cause results to be unpredicatable.
	 */
	private int m_idAccumulator = 0;
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		Feed feed = new Feed();
		feed.run(new Scanner(System.in), System.out);
	}
	
	/**
	 * Main loop
	 * <p>
	 * Run takes Scanner and PrintStreams so that JUnit tests could run 
	 * on the program easily.
	 * 
	 * @param input a scanner to read input lines froms
	 * @param output a print stream to print refresh output to
	 * @throws NumberFormatException thrown when numbers in input are invalid
	 * @throws IOException thrown if there are problems while writing to output
	 */
	public void run(Scanner input, PrintStream output) throws NumberFormatException, IOException {
		// Read feed parameters: (Event Count) (Time window) (Window Height)
		String[] args = input.nextLine().trim().split(" ");
		if(args.length != 3) { throw new IllegalArgumentException("Initial variables: expected 3 arguments, got: " + Integer.toString(args.length)); }
		
		int eventCount = Integer.valueOf(args[0]);
		if(eventCount <= 0) return; // Nothing to do
		
		int timeWindow = Integer.valueOf(args[1]);
		int maxHeight = Integer.valueOf(args[2]);
		
		SortedDensityArray densityArray = new SortedDensityArray(maxHeight, timeWindow);
		SetSearchTree searchTree = new SetSearchTree(densityArray);
		
		String line = null;
		String[] split = null;
		for(int i=0; i < eventCount; i++) {
			line = input.nextLine().trim();
			split = line.split(" ");
			
			if(split.length == 4) { // Add story
				if(split[0].equals("S")) {
					int time = Integer.valueOf(split[1]);
					int score = Integer.valueOf(split[2]);
					int height = Integer.valueOf(split[3]);
					densityArray.add(new Story(time, score, height));
				}
				else { throw new IllegalArgumentException("Expected Story(\"S\"). Got: " + split[0]); }
			}
			else if(split.length == 2) { // Refresh
				if(split[0].equals("R")) {
					int time = Integer.valueOf(split[1]);
					
					String refreshString = searchTree.refresh(time) + "\n";
					output.write(refreshString.getBytes());
					output.flush();
				}
				else { throw new IllegalArgumentException("Expected Refresh(\"R\"). Got: " + split[0]); }
			}
			else {
				throw new IllegalArgumentException("Invalid line, not enough arguments: " +  line);
			}
		}
	}

	/**
	 * Logical Data Class for each story added
	 * <p>
	 * Story class also contains a few helper methods
	 * for determining other information, such as whether
	 * the story is too old (isStale()), score density,
	 * and fractional score.
	 *
	 */
	class Story implements Comparable<Story> {
		private int m_id = -1;
		private int m_height = -1;
		private int m_score = -1;
		private int m_time = -1;
		private double m_density = -1;
		
		public Story(int time, int score, int height) {
			// Avoid divide by zero
			if(height <= 0) { throw new IllegalArgumentException("Stories cannot have 0 height."); }
			
			m_id = ++m_idAccumulator;
			m_height = height;
			m_time = time;
			m_score = score;
			m_density = ((double) score()) / height(); 
		}
		public boolean isStale(int curTime, int window) { return (curTime - m_time) > window ? true : false; }
		public int id() { return m_id; }
		public int time() { return m_time; }
		public int height() { return m_height; }
		public int score() { return m_score; }
		public double density() { return m_density; }
		public double fractionalScore(int remainingHeight) { return density() * remainingHeight; }
		
		@Override
		public int compareTo(Story o) {
			if(this.id() > o.id()) { return 1; }
			else if(this.id() == o.id()) { return 0; }
			else { return -1; }
		}
		
		@Override
		public String toString() {
			return Integer.toString(id());
		}
	}
	
	/**
	 * Sorted Density Array
	 * <p>
	 * This class keeps the stories with the highest
	 * score density first in the array.
	 *
	 */
	class SortedDensityArray {
		private ArrayList<Story> m_stories = new ArrayList<Story>();
		private int m_maxHeight = -1;
		private int m_maxTimeWindow = -1;
		
		public SortedDensityArray(int maxHeight, int maxTimeWindow) {
			m_maxTimeWindow = maxTimeWindow;
			m_maxHeight = maxHeight;
		}
		
		public int maxHeight() { return m_maxHeight; }
		public int maxTimeWindow() { return m_maxTimeWindow; }
		public int size() { return m_stories.size(); }
		
		/**
		 * Get the story at index
		 * 
		 * @param index the index of the story
		 * @return the story
		 */
		public Story get(int index) {
			if(index < 0 || index >= m_stories.size()) { throw new IndexOutOfBoundsException("Index: " + Integer.toString(index)); }
			
			return m_stories.get(index);
		}
		
		/**
		 * Add a story to array based on its density.
		 * 
		 * @param story The story to be added
		 * @return Return True if the add was successful, False if it was not.
		 */
		public boolean add(Story story) {
			if(story == null) { throw new InvalidActionException("Cannot add null story."); }
			if(story.height() > maxHeight()) return false;
			
			// Initial add
			if(m_stories.size() == 0) { 
				m_stories.add(story);
				return true;
			}
			
			// Insert story in the array
			for(int i=0; i < m_stories.size(); i++) {
				if(story.density() > m_stories.get(i).density()) {
					m_stories.add(i, story);
					return true;
				}
			}
			m_stories.add(story); // Insert at end	
			return true;
		}
		
		/**
		 * Remove stories outside time window
		 * 
		 * @param curTime The current time
		 * @return True if stories were pruned, False if not.
		 */
		public boolean prune(int curTime) {
			boolean hasPruned = false;
			for(int i=0; i < m_stories.size(); i++) {
				if(m_stories.get(i).isStale(curTime, maxTimeWindow())) {
					m_stories.remove(i);
					hasPruned = true;
				}
			}
			return hasPruned;
		}
		
		/**
		 * Calculate the current max score allowing for fractional heights.
		 * 
		 * @return The current max score.
		 */
		public double maxFractionalScore() {
			Selection all = new Selection(m_stories.size());
			all.set(0, all.size());
			
			return maxFractionalScore(all);
		}
		
		/**
		 * Calculate the current max score allowing for fractional heights
		 * using the supplied selection of stories. 
		 * 
		 * @param selection a BitSet specifying which stories should be included in
		 * 					the theoretical set.
		 * @return the max score
		 */
		public double maxFractionalScore(Selection selection) {
			double maxScore = 0.0;

			int remainingHeight = m_maxHeight;
			for(int i=selection.nextSetBit(0); i >= 0; i=selection.nextSetBit(i+1)) {
				Story story = m_stories.get(i);
				// Take the full score if story fits; otherwise, take fractional
				if(story.height() > remainingHeight) {
					maxScore += story.fractionalScore(remainingHeight);
					remainingHeight = 0;
				}
				else {
					maxScore += story.score();
					remainingHeight -= story.height();
				}
				
				if(remainingHeight <= 0) { 
					break;
				}
			}
			// Catch unexpected state. Remaining should never be < 0.
			if(remainingHeight < 0) { throw new UnexpectedStateException("Remaining height when getting max fractional should not be < 0."); }
			
			return maxScore;
		}
		
		/**
		 * Calculate the max score with the specified selected stories
		 * 
		 * @param selection	a BitSet where each bit indicates the selection of story in the complete set or not.
		 * @return the max score for the selected stories
		 */
		public int maxScore(Selection selection) {
			// Verify inputs and sizes
			if(selection == null) { throw new NullPointerException("Bit set selection was null when calculting max score."); }
			if(selection.size() > m_stories.size()) { 
				throw new IllegalArgumentException("Bit set selection size was larger than stories set size.");
			}
			
			int maxScore = 0;
			int remainingHeight = maxHeight();
			for(int i=selection.nextSetBit(0); i >= 0; i=selection.nextSetBit(i+1)) {
				// Add up score on selected stories
				maxScore += m_stories.get(i).score();
				remainingHeight -= m_stories.get(i).height();
			}
			// Make sure we didn't blow past max height
			if(remainingHeight < 0) { throw new UnexpectedStateException("Stories selected in calculating max score exceed max height."); }
			
			return maxScore;
		}
		
		/**
		 * Calculate the height of the selected stories
		 * 
		 * @param selection a BitSet where each bit indicates the selection of a story. Note: selection size can be a partial selection of stories
		 * @return
		 */
		public int selectionHeight(Selection selection) {
			// Verify inputs and sizes
			if(selection == null) { throw new NullPointerException("Bit set selection was null when calculating selection's height."); }
			if(selection.size() > m_stories.size()) {
				throw new IllegalArgumentException("Bit set selection size was larger than stories set size.");
			}
			
			// Add up the height of selected stories only
			int height = 0;
			for(int i=selection.nextSetBit(0); i >= 0; i=selection.nextSetBit(i+1)) {
					height += m_stories.get(i).height();
			}
			
			return height;
		}
	
		/**
		 * Get the id string for the selection
		 * 
		 * @param selection a Selection of stories
		 * @return a string of ids sorted in increasing order
		 */
		public String toString(Selection selection) {
			// Get selected stories and sort by ids in increasing order
			Story[] selectedStories = getAllFromSelection(selection);
			Arrays.sort(selectedStories);
			
			String output = "";
			for(int i=0; i < selectedStories.length; i++) {
				output += selectedStories[i].toString() + " ";
			}
		
			return output.trim();
		}
		
		/**
		 * Get the stories selected in the selection and return
		 * them in an array.
		 * 
		 * @param selection a selection of stories
		 * @return an array with the stories selected
		 */
		private Story[] getAllFromSelection(Selection selection) {
			Story[] stories = new Story[selection.selectedCount()];
			for(int i=0, j=selection.nextSetBit(0); i < stories.length && j >= 0; i++, j=selection.nextSetBit(j+1)) {
				stories[i] = m_stories.get(j);
			}
			return stories;
		}
	}
	
	/**
	 * Selection Class
	 * <p>
	 * This class is the logical data unit for a selection of
	 * stories.
	 * <p>
	 * Every node in the search tree will have an instance of 
	 * Selection denoting what set of stories that node has selected.
	 * <p>
	 * The selection class is wrapper of the BitSet class and provides
	 * a defined size for the BitSet. 
	 *
	 */
	class Selection {
		private BitSet m_selection = new BitSet();
		private int m_size = 0;
		
		public Selection() {
			this(0);
		}
		
		public Selection(int size) {
			if(size < 0) { throw new IllegalArgumentException("Selection size cannot be less than 0."); }
			m_size = size;
		}
		
		private Selection(BitSet selection, int size) {
			if(selection == null || size < 0) { throw new NullPointerException(); }
			
			m_selection = selection;
			m_size = size;
		}
		
		/** Copy Constructor **/
		private Selection(Selection copy) {
			m_size = copy.size();
			m_selection = (BitSet) copy.m_selection.clone();
		}
		
		public Selection clone() {
			return new Selection(this);
		}
		
		public int size() { return m_size; }
		
		/**
		 * Get the number of selected stories in this selection
		 * 
		 * @return the number selected
		 */
		public int selectedCount() {
			if(size() == 0) { return 0; }
			
			int count = 0;
			for(int i=m_selection.nextSetBit(0); i >= 0; i=m_selection.nextSetBit(i+1)) {
				count++;
			}
			return count;
		}
		
		/**
		 * Set selection at index to TRUE
		 * <p>
		 * If index is greater than size, the size will
		 * be increased
		 * 
		 * @param index an Index
		 */
		public void set(int index) {
			set(index, true);
		}

		/**
		 * Set selection from fromIndex (inclusive) to toIndex (exclusive)
		 * to True.
		 * 
		 * @param fromIndex index to start setting from (inclusive)
		 * @param toIndex index after last story to set
		 */
		public void set(int fromIndex, int toIndex) {
			set(fromIndex, toIndex, true);
		}
		
		/**
		 * Set selection at index to value
		 * 
		 * @param index index of story to set
		 * @param value the value to set the selection to.
		 */
		public void set(int index, boolean value) {
			set(index, index+1, value);
		}
		


		/**
		 * Set the values from fromIndex (inclusive) to toIndex (exclusive to value.
		 * 
		 * @param fromIndex index to start setting from
		 * @param toIndex index after last story to set
		 * @param value the value to set all selections to
		 */
		public void set(int fromIndex, int toIndex, boolean value) {
			if(fromIndex < 0 || toIndex < 0 || fromIndex > toIndex) { throw new IndexOutOfBoundsException(); }
			if(toIndex >= m_size) {
				m_size = m_size + (toIndex - m_size);
			}
			m_selection.set(fromIndex, toIndex, value);
		}
		
		/**
		 * Get the value at the index. index must be less than size()
		 * 
		 * @param index the index in the selection
		 * @return the selection status
		 */
		public boolean get(int index) {
			if(index < 0 || index >= size()) { 
				throw new
				IndexOutOfBoundsException("Size: " + Integer.toString(m_size) + "  Index: " + Integer.toString(index));
			}
			return m_selection.get(index);
		}
		
		/**
		 * Get a new subset selection from fromIndex (inclusive) to toIndex (exclusive)
		 * 
		 * @param fromIndex index of first selection to include
		 * @param toIndex index after last selection to include
		 * @return a new selection with matching size of selection
		 */
		public Selection get(int fromIndex, int toIndex) {
			if(fromIndex < 0 || toIndex < 0 || fromIndex > toIndex) { throw new IndexOutOfBoundsException(); }
			
			int size = toIndex - fromIndex;
			BitSet selection = m_selection.get(fromIndex, toIndex);
			
			return new Selection(selection, size);
		}
		
		/**
		 * Return next bit set to true starting from fromIndex (inclusive)
		 * 
		 * @param fromIndex the index to start searching from (inclusive)
		 * @return the index of the next bit set to true. If no more are true, returns -1.
		 */
		public int nextSetBit(int fromIndex) {
			return m_selection.nextSetBit(fromIndex);
		}
		
		/**
		 * Create a hypothetical selection where everything up to current
		 * is as is, but then assume we select all remainder stories up till
		 * size.
		 * 
		 * @param size the final size of the new selection
		 * @return a new selection where 'cur' stories are selected and rest are assumed selected
		 */
		public Selection fractionalize(int size) {
			if(size <= 0) { throw new IllegalArgumentException("Size cannot be <= 0"); }
			if(size() > size) { throw new IllegalArgumentException("Current selection cannot be larger than requested size."); }
			
			Selection ret = clone();
			ret.set(ret.size(), size);
			return ret;
		}
		
		/**
		 * Output the selection to a binary string.
		 * 
		 * Example: "0010110"
		 */
		public String toString() {
			String out = "";
			for(int i=0; i < size(); i++) {
				out += get(i) ? "1" : "0";
			}
			return out;
		}
	}

	/**
	 *  The search tree
	 *	<p>
	 *	This class is used for performing a search for the
	 *	best optimal set of stories to show in a feed.
	 *	<p>
	 *	A search can be done using the refresh() method.
	 *	<p>
	 *	The search tree requires a filled density array
	 *	in order to word.
	 */
	class SetSearchTree {
		private SortedDensityArray m_array = null;
		
		private RootNode m_root = null;
		
		/**
		 * Default Constructor
		 * 
		 * @param array an array with all available stories
		 */
		public SetSearchTree(SortedDensityArray array) {
			if(array == null) { throw new NullPointerException("Search Tree cannot be created with null array."); }
			
			m_array = array;
		}
		
		/**
		 * Refresh, getting the best selection of stories to show
		 * 
		 * @param curTime the current time the refresh was requested
		 * @return a valid output string in form: <maxScore:int> <storyCount:int> <id> <id> ...
		 */
		public String refresh(int curTime) {
			m_array.prune(curTime);
			
			if(m_array.size() == 0) { return "0 0"; }
			else if(m_array.size() < 0) { throw new UnexpectedStateException("Story array size was < 0"); }
			
			// Refresh root node with new fractional max
			m_root = new RootNode(m_array.maxFractionalScore(), m_array);
			
			
			SearchDeciderStack searchStack = new SearchDeciderStack();
			SearchResultSet resultSet = new SearchResultSet();
			
			
			/* Flags for CreateNode */
			boolean LEFT = true;
			boolean RIGHT = false;
			
			// Push root branches, can't push root due to root not having a selection state
			if(m_root.left() != null) { searchStack.push(m_root.left()); }
			if(m_root.right() != null) { searchStack.push(m_root.right()); }
			
			boolean createResult = false;
			/*
			 * Loop until either:
			 * 		Our result set's max score beats the current theoretical max
			 * OR
			 * 		Our search stack hits 0
			 */
			while(resultSet.curMaxScore() <= searchStack.currentMaxFractional() &&
					searchStack.size() > 0) {
				SearchTreeNode node = searchStack.pop();
				
				createResult = createBranchNode(node, RIGHT);
				if(createResult) {
					searchStack.push(node.right());
				}
				else {
					resultSet.add(node);
				}
				
				createResult = createBranchNode(node, LEFT);
				if(createResult) {
					searchStack.push(node.left());
				}
				else {
					resultSet.add(node);
				}
			}
			
			SearchTreeNode[] results = resultSet.result();
			SearchTreeNode resultNode = null;
			if(results.length <= 0) { return "0 0"; } // No results
			else if(results.length == 1) { resultNode = results[0]; }
			else {
				/*
				 * Multiple sets have same score and story count,
				 *  perform lexicographical comparison
				 */
				SearchTreeNode currentBest = null;
				for(int i=0; i < results.length; i++) {
					if(currentBest == null) {
						currentBest = results[i];
						continue;
					}
					else {
						currentBest = lexicographicTest(currentBest, results[i]);
					}
				}
				resultNode = currentBest;
			}
			
			int maxScore = m_array.maxScore(resultNode.selection());
			int count = resultNode.selectedCount();
			String ids = m_array.toString(resultNode.selection());
			String out = Integer.toString(maxScore) + " " + Integer.toString(count) + " " + ids;
			return out;
		}
		
		/**
		 * Compare to nodes and find the one with the lowest order
		 * lexographical story set.
		 * 
		 * @param o1 a search node to compare
		 * @param o2 a search node to compare
		 * @return the node with lowest lexographical order
		 */
		private SearchTreeNode lexicographicTest(SearchTreeNode o1, SearchTreeNode o2) {
			String ids1 = m_array.toString(o1.selection());
			String ids2 = m_array.toString(o2.selection());
			
			if(ids1.compareTo(ids2) < 0) { return o1; }
			else if(ids1.compareTo(ids2) > 0) { return o2; }
			else {
				throw new UnexpectedStateException("Two selections should never be lexicographically the same!");
			}
		}
		
		/**
		 * Create the next branch node based on the selection stored in node.
		 * <p>
		 * If a node is created then this returns true, meaning there may be more nodes following.
		 * If a node can't be created then this returns false, meaning its a leaf node.
		 * 
		 * @param node the node to create a left node on
		 * @param isLeft true if this is the left branch, false if its the right
		 * @return True if it was created. False if it wasn't, node is a leaf node
		 */
		private boolean createBranchNode(SearchTreeNode node, boolean isLeft) { 
			SearchTreeNode child = isLeft ? node.left() : node.right();
			
			if(child == null) {
				// Check if there are more selections possible
				if(node.selection().size() < m_array.size()) {
					
					// Increase selection by 1 and set it depending on path
					Selection selection = node.selection().clone();
					int index = selection.size(); // Index in selection for new node
					selection.set(index, isLeft);
					
					// Create node with new selection
					child = SearchTreeNode.createNode(m_array, node.score(), node.height(), selection);
					if(isLeft) { node.setLeft(child); }
					else { node.setRight(child); }
					
					return (child == null) ? false : true;
				}
			}
			return false;
		}
	}
	
	/**
	 * A set of best possible Search node selections
	 * <p>
	 * This class is used during a search to save the
	 * leaf nodes as they are found.
	 * <p>
	 * The result set tracks the current max score and
	 * the minimum story count of a set.
	 * <p>
	 * The result set can't tell the Search Tree when a 
	 * search is finished, but the result set CAN provide
	 * information that lets the search tree know when it
	 * should stop searching for more possible sets.
	 * 
	 */
	class SearchResultSet {
		private TreeSet<SearchTreeNode> nodes = new TreeSet<SearchTreeNode>();
		
		private int m_curMaxScore = 0;
		private int m_curMinSelectedCount = -1;
		
		public int curMaxScore() { return m_curMaxScore; }
		
		/**
		 * Try to add a node to the set
		 * 
		 * @param node a node to add 
		 * @return return True if the add was successful, False if the node was rejected by the
		 * 	set for not being good enough.
		 */
		public boolean add(SearchTreeNode node) {
			if(node == null) { throw new NullPointerException(); }
			
			/*
			 * Order of precedence is: 
			 * 		Max Score
			 * 		Story Count
			 * 		String lexicographicallity
			 */
			if(node.score() < m_curMaxScore) { return false; }
			else if(node.score() == m_curMaxScore) {
				// Check if node's selected count beats current
				if(node.selectedCount() < m_curMinSelectedCount || m_curMinSelectedCount == -1) {
					nodes.clear();
					nodes.add(node);
					m_curMinSelectedCount = node.selectedCount();
				}
				else if(node.selectedCount() > m_curMaxScore) { return false; } // Don't add node that has more stories & equal score
				else{ // Can't do lexicographical test, add and do at final result
					nodes.add(node);
				}
			}
			else { // Greater, remove all prior results
				nodes.clear();
				nodes.add(node);
				m_curMaxScore = node.score();
				m_curMinSelectedCount = node.selectedCount();
			}
			return true;
		}
		
		/**
		 * Gather the results currently held in the set
		 * 
		 * @return an array of search nodes
		 */
		public SearchTreeNode[] result() {
			SearchTreeNode[] results = new SearchTreeNode[nodes.size()];
			nodes.toArray(results);
			return results;
		}
	}
	
	/**
	 * Search Decider provides a way to choose the next
	 * best node to check for a set solution when performing
	 * a refresh.
	 * <p>
	 * As nodes are created, they are pushed onto the internal
	 * stack. The Decider will insert the node in the stack where
	 * it best fits. So every pop of the stack always guarantees
	 * that that node is the next best option to search.
	 * <p>
	 * The search loop can make calls to currentMaxFractional()
	 * to get what the best (next pop()) node on the stack COULD
	 * yield on its path.
	 *
	 */
	class SearchDeciderStack {
		private TreeSet<SearchTreeNode> m_stack = new TreeSet<SearchTreeNode>(new DeciderComparator());
		private double m_currentFractionalScore = 0.0;
		
		public double currentMaxFractional() {
			return m_currentFractionalScore;
		}
		
		public int size() { return m_stack.size(); }
		
		public boolean push(SearchTreeNode node) {
			if(node == null) { throw new NullPointerException("Cannot add null node to decider stack."); }
			
			double nodeFracScore = node.maxFractionalScore();
			boolean addResult = m_stack.add(node);
			if(addResult) {
				if(nodeFracScore > m_currentFractionalScore) {
					setCurrentMaxFractional(nodeFracScore);
				}
			}
			
			return addResult;
		}
		
		/**
		 * Pop the current node with highest likely score path.
		 * 
		 * @return returns the highest node on success; returns null if the stack is empty
		 */
		public SearchTreeNode pop() {
			if(size() > 0) {
				SearchTreeNode node = m_stack.first();
				m_stack.remove(node);
				if(size() > 0) {
					setCurrentMaxFractional(m_stack.first().maxFractionalScore());
				}
				return node;
			}
			else {
				return null;
			}
		}
		
		private void setCurrentMaxFractional(double score) {
			m_currentFractionalScore = score;
		}
		
		
		class DeciderComparator implements Comparator<SearchTreeNode> {

			@Override
			public int compare(SearchTreeNode o1, SearchTreeNode o2) {
				double o1Score = o1.maxFractionalScore();
				double o2Score = o2.maxFractionalScore();
				if(o1Score > o2Score) { return -1; }
				else if(o1Score < o2Score) { return 1; }
				
				return 0;
			}
			
		}
	}
	
	
	class RootNode extends SearchTreeNode { 
		
		public RootNode(double maxFractionalScore, SortedDensityArray array) {
			super(null);
			m_maxFractionalScore = maxFractionalScore;
			
			// Set up Left and Right branch
			Selection selection = new Selection(1);
			selection.set(0, true);
			SearchTreeNode left = SearchTreeNode.createNode(array, 0, 0, selection);
			if(left != null) { setLeft(left); }
			
			selection = new Selection(1);
			selection.set(0, false);
			SearchTreeNode right = SearchTreeNode.createNode(array, 0, 0, selection);
			if(right != null) { setRight(right); }
		}
	}
	
	/**
	 * The class for a node on a search tree
	 * <p>
	 * Each node contains the current selection of
	 * stories up to that node, along with the current
	 * height and score of the selection.
	 * <p>
	 * The node also contains the theoretical max score
	 * that can be made if all stories after this node are
	 * selected. This score is not guaranteed but helps by
	 * suggesting the best possible route.
	 *
	 */
	static class SearchTreeNode implements Comparable<SearchTreeNode> {
		/**
		 * The new theoretical fractional score based on selections
		 * <p>
		 * It's important to note that this can change 
		 */
		protected double m_maxFractionalScore = 0.0; 
		
		/**
		 * The current selections up to this node.
		 * <p>
		 * For example, given the selection of array { x_1, x_2, x_3 }.
		 * If this selection were { 1, 0, 1 }, this would be a leaf node
		 * where we have chosen x_1 and x_3 to be in the set.
		 */
		private Selection m_selection = null;
		
		/**
		 * The total score up to this node based on the selections
		 */
		private int m_curScore = 0;
		
		/**
		 * The total height up to this node based on the selections
		 */
		private int m_curHeight = 0;
		
		/**
		 * The total selected stories up to this node
		 */
		private int m_selectedCount = 0;
		
		private SearchTreeNode m_left = null;
		private SearchTreeNode m_right = null;
				
		protected SearchTreeNode(Selection selection) {
			m_selection = selection;
		}
		
		protected Selection selection() { return m_selection; }
		protected int selectedCount() { return m_selectedCount; }
		protected int score() { return m_curScore; }
		protected int height() { return m_curHeight; }
		protected double maxFractionalScore() { return m_maxFractionalScore; }
		
		
		protected SearchTreeNode left() { return m_left; }
		protected SearchTreeNode right() { return m_right; }
		protected void setLeft(SearchTreeNode node) { m_left = node; }
		protected void setRight(SearchTreeNode node) { m_right = node; }
		
		@Override
		public int compareTo(SearchTreeNode o) {
			// Check for which has max score
			if(this.score() > o.score()) {
				return 1;
			}
			else if(this.score() < o.score()) {
				return -1;
			}
			else {
				// (If equal) check for which has fewest items
				if(this.selectedCount() > o.selectedCount()) {
					return 1;
				}
				else if(this.selectedCount() < o.selectedCount()) {
					return -1;
				}
				else {
					// Check lexicography at final result
					return 0;
				}
			}
		}
		
		/**
		 * Create a new node
		 * 
		 * @param array the current story density array
		 * @param prevScore the score from the previous node
		 * @param prevHeight the height from the previous node
		 * @param curSelection the selection for the NEW node
		 * @return a new node if the new selection fits within the maxHeight, otherwise return null
		 */
		protected static SearchTreeNode createNode(SortedDensityArray array, int prevScore, int prevHeight, Selection curSelection) {
			if(array == null || prevScore < 0 || prevHeight < 0) {
				throw new IllegalArgumentException("Cannot create new node. Invalid parameters.");
			}			
			SearchTreeNode newNode = new SearchTreeNode(curSelection);
			int newNodeIndex = curSelection.size()-1; // Get this node's selection index
			boolean isSelected = curSelection.get(newNodeIndex); // Should we consider this story selected?
			
			newNode.m_curHeight = isSelected ? (prevHeight + array.get(newNodeIndex).height()) : prevHeight;
			// Are we going over maxHeight?
			if(newNode.height() > array.maxHeight()) { return null; }
			
			newNode.m_curScore = isSelected ? (prevScore + array.get(newNodeIndex).score()) : prevScore;
			newNode.m_maxFractionalScore = array.maxFractionalScore(curSelection.fractionalize(array.size()));
			newNode.m_selectedCount = newNode.m_selection.selectedCount();
			
			return newNode;
		}
		
		/**
		 * Helper function to give a string with the node's stats.
		 * 
		 * States are Height, Score, Fractional Score(Upper-bound Score)
		 * 
		 * @return a string with the nodes stats
		 */
		public String stats() {
			StringBuilder builder = new StringBuilder();
			builder.append("H(" + m_curHeight + ") ");
			builder.append("S(" + m_curScore + ") ");
			builder.append("US(" + m_maxFractionalScore + ")");
			return builder.toString();
		}
	}
	
	/**
	 * Action was invalid or supplied arguments were invalid
	 *
	 */
	@SuppressWarnings("serial")
	class InvalidActionException extends RuntimeException {
		public InvalidActionException(String s) {
			super(s);
		}
	}
	
	/**
	 * Something unexpected happened
	 *
	 */
	@SuppressWarnings("serial")
	class UnexpectedStateException extends RuntimeException {
		public UnexpectedStateException(String s) {
			super(s);
		}
	}
}
