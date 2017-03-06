/*
THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
CODE WRITTEN BY OTHER STUDENTS. ___ANDREW C JONES 2/14/17___
*/

/*the formula is as follows -- map[N] is equal to (map[N-1] + K)%N */
public class Mary {

	/*where N is the size of the list (list spans from 1 to N inclusive), 
	and every Kth position in the list is eliminated, this function returns
	the last position to be eliminated. there is no requirement that K < N; function will work fine
	if N < K. */
	public static int mary(int N, int K) {

		//map stores all answers from one to N so that recursion is not necessary.
		//every Nsub n will be computed based on map[n-1] until N is reached, the function will then return
		//map[N]
		if(N == 0 || K == 0) {
			return -1;
		}
		int[] map = new int[N];

		//"base case" -- the answer when n is 0 is always 0
		map[0] = 0;

		//loop start at 1 because value for 0 is given. continues through N inclusive.
		for(int n = 1; n < N; n++) {

			//see comment at top of file: f(N) = (f(N-1) + K)%N
			map[n] = (map[n-1] + K) % (n+1);
		}

		//plus 1 because problem list begins at 1 not zero
		return map[N-1] + 1;
	}


	public static void main(String[] args) {
		System.out.println(Mary.mary(7, 2));
	}
}