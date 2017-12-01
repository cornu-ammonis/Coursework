/*
THIS CODE WAS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
CODE WRITTEN BY OTHER STUDENTS OR SOURCES OUTSIDE OF THOSE
PROVIDED BY THE INSTRUCTOR.  _Your Name_Here_
*/

public class Automata {

	// stateByTransition[x][y] = output state if starting at state x and input is y
	private int[][] stateByTransition;

	private boolean[] inPattern;

	private String patternString; 

	// Constructor which builds an automata for patternString
	public Automata(String pattern) {

		patternString = pattern;

		stateByTransition = new int[ patternString.length() + 1] [ 128 ];

		inPattern = new boolean[ 128 ];

		// TO DO: add try catch with helpful error message if charcter not in ascii
		for (int i = 0; i < patternString.length(); i++) 
			inPattern[ (int) patternString.charAt(i) ] = true;

		// we loop to the last chracter but not to the last position in the stateByTransition array becasue that last position is 
		for (int i = 0; i < patternString.length(); i++) {

			// c represents each ascii value
			for (int c = 0; c < 128; c++ ) {

				if ( inPattern[c] ) {
					// this means that the character is the correct one for subsequent position in string
					// therefore the state is the current state + 1
					if (c == (int) patternString.charAt(i) ) 
						stateByTransition[i][c] = i + 1;

					else 
						stateByTransition[i][c] = prefixSuffixOverlap(i, c);

					
				}

			}
		}

		for (int c = 0; c < 128; c++)
			stateByTransition [ patternString.length() ] [c] = patternString.length();



	}

	private int prefixSuffixOverlap(int i, int c) {
		String possibleString = "";

		for (int j = 0; j < i; j++) 
			possibleString += patternString.charAt(j);

		possibleString += (char) c;

		return prefixSuffixOverlapCount(possibleString);
		
	}


	private int prefixSuffixOverlapCount(String string) {

		int[] dp = new int[ string.length() ];

		int i = 1;
		int length = 0;

		while ( i < string.length() ) {
			if ( string.charAt(i) == string.charAt(length) ) {
				
				length += 1;
				dp[i] = length;
				i += 1;

			}

			else { // string[i] != string[length]

				if ( length == 0 ) {
					dp[i] = 0;
					i += 1;
				}

				else
					length = dp[ length - 1 ]; 
			}
		}

		return dp[ string.length() - 1 ];



	}

	public int transition(int i , char c) {

		return stateByTransition[i] [(int) c];

	}

	
}