/*
THIS CODE WAS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
CODE WRITTEN BY OTHER STUDENTS OR SOURCES OUTSIDE OF THOSE
PROVIDED BY THE INSTRUCTOR.  _Your Name_Here_
*/

public class Automata {

	// stateByTransition[x][y] = output state if starting at state x and input is y
	private int[][] stateByTransition;

	private boolean[] inPattern;

	// this is here for easier accesibility for helper methods (avoid redundant parameters)
	private String patternString; 

	// Constructor which builds an automata using pattern
	public Automata(String pattern) {

		// set private field
		patternString = pattern;

		// initialized to be (m+1)*k 
		stateByTransition = new int[ patternString.length() + 1] [ 128 ];

		// indiciates which characters in the ascii alphabet are in the pattern
		// if they are not in the pattern, we know their input will always result
		// in a transition to state zero, making this assumption (and relying on
		// the fact that int arrays have default element values of 0)
		// can save us some time in constructing the automata
		inPattern = new boolean[ 128 ];

		// handles exception where pattern contains a non-ascii character
		try {
			for (int i = 0; i < patternString.length(); i++) 
				inPattern[ (int) patternString.charAt(i) ] = true;
		}

		// this exception will be an array index out of bounds error which is less helpful than the exception i provide
		catch(Exception e) { 
			throw new IllegalArgumentException("pattern contains a non-ascii character");
		}
		

		// we loop to the last chracter but not to the last position in the stateByTransition array becasue that last state is final
		for (int i = 0; i < patternString.length(); i++) {

			// c represents each ascii value
			for (int c = 0; c < 128; c++ ) {

				// only proceed for characters present in the pattern string; leave any others at default 0 value
				if ( inPattern[c] ) {
					
					// this means that the character is the correct one for subsequent position in string
					// therefore transition state is the current state + 1
					if (c == (int) patternString.charAt( i ) ) 
						stateByTransition[ i ][ c ] = i + 1;

					// not the subsequent character in the pattern meaning transition state 
					// is either 0 or the maximum prefix-suffix overlap length
					else 
						stateByTransition[ i ][ c ] = prefixSuffixOverlap(i, c);

					
				}

			}
		}

		// not strictly necessary, but for completeness we 
		// set the final state to return to itself given every ascii input
		for (int c = 0; c < 128; c++)
			stateByTransition [ patternString.length() ] [c] = patternString.length();



	}

	// creates a new string -- possilbeString -- which represents a string which matched the pattern
	// up until this most recent character c. it then uses the prefixSuffixOverlapCount helper method 
	// to determine the longest prefix which is also a suffix of this string. 
	//
	// the purpose of this computation is to determine whether a mismatch sends the automata 
	// back to state zero or instead sends it to an intermediate state which is equal to 
	// prefixSuffixOverlapCount
	private int prefixSuffixOverlap(int i, int c) {
		String possibleString = "";

		for (int j = 0; j < i; j++) 
			possibleString += patternString.charAt(j);

		possibleString += (char) c;

		return prefixSuffixOverlapCount(possibleString);
		
	}


	// determines the longest prefix which is also a suffix of this string. 
	//
	// the purpose of this computation is to determine whether this mismatch sends the automata 
	// back to state zero or instead sends it to an intermediate state which is equal to 
	// prefixSuffixOverlapCount. 
	// 
	// Works via similar logic to KMP algorithm and partially derived from it
	private int prefixSuffixOverlapCount(String string) {

		// similar to lps array in KMP algorithm
		int[] dp = new int[ string.length() ];

		int i = 1;
		int length = 0;

		while ( i < string.length() ) {

			// possible prefix-suffix match
			if ( string.charAt(i) == string.charAt(length) ) {
				
				length += 1;
				dp[i] = length;
				i += 1;

			}

			// no match here
			else { // string[i] != string[length]

				if ( length == 0 ) { // no possible prefix/suffix for this position
					dp[i] = 0;
					i += 1;
				}

				else // a shorter prefix / suffix may be valid 
					length = dp[ length - 1 ]; 
			}
		}

		// equivalent to the length of the longest valid prefix which is also a suffix
		return dp[ string.length() - 1 ];



	}

	// returns the state of the automata given that it starts from state i and 
	// receives character c as input
	public int transition(int i , char c) {

		return stateByTransition[ i ] [ (int) c];

	}

	// returns true if the automata is in a final state. for this automata the state is final
	// if it reached the last available state, which is equivalent to having matched the pattern
	public boolean isFinal(int state) {
		return state == patternString.length();
	}
	
}