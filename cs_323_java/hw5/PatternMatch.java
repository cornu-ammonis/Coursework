import java.util.Scanner; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PatternMatch {

	public static void main(String[] args) {

		// validate correct number of arguments
		if (args.length != 1) {
			System.out.println("Usage: one command line argument specifying text file name for pattern searching");
			System.exit(0);

		}

		String fileName = args[0];


		// citation: this pattern for reading text file line-by-line taken from the following tutorial:
		// http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html
		try {
			
			// Check file exists
			File file = new File(fileName);
			if (file.exists() == false)
				throw new IllegalArgumentException("file " + fileName +  " does not exist\n");

			// get input pattern
			Scanner scan = new Scanner(System.in);
			System.out.println("enter a pattern");
			String s = scan.next();

			// Construct Automata and set default state
			Automata automata = new Automata(s);
			int state = 0;

			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;
			int lineNumber = 1;
			while ((line = bufferedReader.readLine()) != null ) {

				for (int i = 0; i < line.length(); i++) {
					state = automata.transition( state, line.charAt(i) );
				}

				if (automata.isFinal( state )) {
					System.out.println("[line " + lineNumber + "]");
					System.out.println(line);
					state = 0; // revert to starting state
				}

				lineNumber++;

			}
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
}