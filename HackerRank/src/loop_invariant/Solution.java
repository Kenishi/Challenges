package loop_invariant;

import java.util.*;

/*
 * Loop Invariant problem
 * 
 * The problem is that the while loop ignores checking the
 * first element (0) in the array and so it won't shift that
 * first element forward.
 * 
 * Ex: 4 3 2 1
 * On the first iteration, basically the nothing changes
 * because 3's previous index is 0 and the while loop won't
 * run, so the iteration basically replaces it self with it self.
 * 
 * Make the while run till >= 0 to fix this problem.
 */

public class Solution {
    
    public static void insertionSort(int[] A){
	  for(int i = 1; i < A.length; i++) {
	    int value = A[i];
	    int prevIndex = i - 1; 
	    
	    // Loop till we hit 0 and while the prev value
	    // is greater than value we are sorting
	    
	    while(prevIndex >= 0 && A[prevIndex] > value) {
	    	/*
	    	 * Value is greater, so shift prev forward by one
	    	 */
	    	A[prevIndex + 1] = A[prevIndex];
	    	prevIndex = prevIndex - 1;
	    }
	    
	    A[prevIndex + 1] = value;
	  }
	        
	  printArray(A);
    }

    
    static void printArray(int[] ar) {
         for(int n: ar){
            System.out.print(n+" ");
         }
      }
public static void main(String[] args) {
           Scanner in = new Scanner(System.in);
           int n = in.nextInt();
           int[] ar = new int[n];
           for(int i=0;i<n;i++){
              ar[i]=in.nextInt(); 
           }
           insertionSort(ar);
       }    
   }
