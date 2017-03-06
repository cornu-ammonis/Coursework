/*
THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
CODE WRITTEN BY OTHER STUDENTS. ___ANDREW C JONES 2/14/17___
*/


public class Mary {

	//where N is the size of the list (list spans from 1 to N inclusive), 
	//and every Kth position in the list is eliminated, this function returns
	//the last position to be eliminated. there is no requirement that K < N; function will work fine
	//if N < K. 
	public int mary(int N, int K) {

		//map stores all answers from one to N so that recursion is not necessary.
		//every Nsub n will be computed based on map[n-1] until N is reached, the function will then return
		//map[N]
		int[] map = new int[N+1];

		//"base case" -- the answer when n is 1 is always 1
		map[1] = 1;

		//loop start at 2 because value for 1 is given. continues through N inclusive.
		for(int n = 2; n <= N; n++) {


			//the formula is as follows -- map[N] is equal to (map[N-1] + K)%N
			//if the answer to that formula is 0, which does not exist as the problem begins at 1,
			//you intead "loop back around" and choose the last position in the array, which is N. 
			//this is handled by the if condition.

			if((map[n-1] + K)%n == 0) {
				map[n] = n;
			}
			else {
				map[n] = (map[n-1] + K)%n;
			}
		}

		return map[N];
	}


	public static void main(String[] args) {

		Mary m = new Mary();
		System.out.println(m.mary(2, 3));
	}
}