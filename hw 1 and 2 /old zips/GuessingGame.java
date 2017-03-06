/*
THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
CODE WRITTEN BY OTHER STUDENTS. ___ANDREW C JONES ___
*/

import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

public class GuessingGame {

	//data structure containing all remaining valid guesses
	private ArrayList<Integer> list;
	
	//tracks the number of guesses so far 
	private int numGuesses;
	
	//instance of Random used to produce a random number
	private Random r;
	
	//persists the most recent guess, used to update list 
	private int mostRecentGuess;

	//constructor for the GuessingGame class
	public GuessingGame() {
		
		//initializes list as an array of Integers not int because ArrayList requires reference type
		list = new ArrayList<Integer>();

		//initializes the list to contain all numbers from 1000 to 9999
		for(int i = 1000; i < 10000; i++) {
			list.add(i);
		}

		//initializes r as an instance of the Random class, which is used to produce a random number for guess
		r = new Random();

	}

	public int myGuessIs() {
		
		//if list has fewer than 1 remaining element, something went wrong, return -1
		if(list.size() == 0) {
			return -1;
		}

		//calls r.nextInt with the size of the list as the upper bound -- so r.nextInt will return a 
		//pseudorandom number between 0 and the size of the array (exclusive)
		int g = r.nextInt(list.size());

		//stores the most recent guess to be used in the updateMyGuess function
		mostRecentGuess = (int) list.get(g);
		
		//increments the current number of guesses
		numGuesses = numGuesses + 1;
		
		//returns the guess
		return mostRecentGuess;
	}
	
	//returns total number of guesses so far -- used to display number of guesses used at completion of game
	public int totalNumGuesses() {
		return numGuesses;
	}
 
 	//eliminates invalid guesses based on the number of matching digits between most recent guess 
 	//and secret answer 
	public void updateMyGuess(int nmatches) {
		
		//loops through the array backwards to remove elements; looping forwards would be problematic because 
		//removing an item in the middle of the array would shift all subsequent element indexes back by one, 
		//making the condition (eg i < list.size()) invalid
		for (int i = list.size() - 1; i >=0; i--) {
			
			/* for each element in the arraylist, if the number of matching digits between that element
			and the most recent guess does not equal nmatches (the number of matching digits between the 
			most recent guess and the secret answer), remove that element */
			if(numMatch(mostRecentGuess, (int) list.get(i)) != nmatches) {
				list.remove(i);
			}
		}
	}


		
	
	// fill in code here (optional)
	// feel free to add more methods as needed

	//helper function which returns the number of matching digits between int a and int b 
	public int numMatch(int a, int b) {
		int count = 0;
		while(a > 0 && b > 0) {
			if(a%10 == b%10) {
				count = count + 1;
			}

			a = a/10;
			b = b/10;

		}
		return count;

	}


	
	// you shouldn't need to change the main function
	public static void main(String[] args) {

		GuessingGame gamer = new GuessingGame( );
  
		JOptionPane.showMessageDialog(null, "Think of a number between 1000 and 9999.\n Click OK when you are ready...", "Let's play a game", JOptionPane.INFORMATION_MESSAGE);
		int numMatches = 0;
		int myguess = 0;
		
		do {
			myguess = gamer.myGuessIs();
			if (myguess == -1) {
				JOptionPane.showMessageDialog(null, "I don't think your number exists.\n I could be wrong though...", "Mistake", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
			String userInput = JOptionPane.showInputDialog("I guess your number is " + myguess + ". How many digits did I guess correctly?");
			// quit if the user input nothing (such as pressed ESC)
			if (userInput == null)
				System.exit(0);
			// parse user input, pop up a warning message if the input is invalid
			try {
				numMatches = Integer.parseInt(userInput.trim());
			}
			catch(Exception exception) {
				JOptionPane.showMessageDialog(null, "Your input is invalid. Please enter a number between 0 and 4", "Warning", JOptionPane.WARNING_MESSAGE);
				continue;
			}
			// the number of matches must be between 0 and 4
			if (numMatches < 0 || numMatches > 4) {
				JOptionPane.showMessageDialog(null, "Your input is invalid. Please enter a number between 0 and 4", "Warning", JOptionPane.WARNING_MESSAGE);
				continue;
			}
			if (numMatches == 4)
				break;
			// update based on user input
			gamer.updateMyGuess(numMatches);
			
		} while (true);
		
		// the game ends when the user says all 4 digits are correct
		System.out.println("Aha, I got it, your number is " + myguess + ".");
		System.out.println("I did it in " + gamer.totalNumGuesses() + " turns.");
	}
}
