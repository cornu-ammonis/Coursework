/*
THIS CODE WAS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
CODE WRITTEN BY OTHER STUDENTS OR SOURCES OUTSIDE OF THOSE
PROVIDED BY THE INSTRUCTOR.  _Your Name_Here_
*/

public class Automata {

	// stateByTransition[x][y] = output state if starting at state x and input is y
	private int[][] stateByTransition;

	private boolean[] inPattern;

	// Constructor which builds an automata for patternString
	public Automata(String patternString) {
		
		stateByTransition = new int[ patternString.length() ] [ 128 ];

		inPattern = new boolean[ 128 ];

		// TO DO: add try catch with helpful error message if charcter not in ascii
		for (int i = 0; i < patternString.length(); i++) 
			inPattern[ (int) patternString.charAt(i) ] = true;

		
	}
}