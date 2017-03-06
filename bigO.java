public class bigO {
	
	public static int testEx1(int n) {
	int sum = 0;
	for( int i = n; i > 0; i /= 2 ) {
  		for( int j = 1; j < n; j *= 2 ) {
    		for( int k = 0; k < n; k += 2 ) {
          sum = sum + 1;
    }
  }
}
		return sum;

	}

	public static int testEx2(int n) {
		int sum = 0;
		for(int i = 1; i < n; i*= 2) {
			for(int j = n; j > 0; j /= 2) {
				for(int k = j; k < n; k += 2) {
					sum++;
				}
			}
		}

		return sum;
	}


public static void main(String[] args) {


	System.out.println(testEx2(1000));
}

}