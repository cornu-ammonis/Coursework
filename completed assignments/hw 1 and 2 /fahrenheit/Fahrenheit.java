
//class with static method toCelsius which converts farenheit to celsius, and static method Test which 
//runs test cases on toCelsius 
public class Fahrenheit{

	//converts farenheit temperature to celsius. expects a double greater than -460
	public static double toCelsius (double fahrenheitTemp) {
		

		//rejects invalid input -- farenheit absolute zero is -459.67 
		if (fahrenheitTemp < -460) {
			throw new UnsupportedOperationException("That input is below absolute zero.");
		} 


		double celsiusTemp = (fahrenheitTemp - 32)/ 1.8;
		return celsiusTemp;
	}


	public static boolean TestConversion() {
		
		//checks that 32f correctly converts to 0c
		if(toCelsius(32) != 0){
			System.out.println("32f doesnt equal 0c");
			return false;

		}


		//checks that uses proper formula
		for(int i = -458; i < Integer.MAX_VALUE; i = (int)Math.pow(i, 2)){
			if(toCelsius(i) != ((double)i - 32)/1.8) {
				return false;
			}
			//System.out.println(i);
		}

		return true;


	}

	//should result in UnsupportedOperationException when called, confirming that function rejects values below absolute zero
	public static void TestException() {
		double result = toCelsius(-470);
	}
}

