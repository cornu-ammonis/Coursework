
import java.util.ArrayList;
import java.util.Random;
public class numMatch {
	
	public static int numMatches(int a, int b) {
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

	public int[] whichIndexesMatch(int a, int b) {
		int[] result = new int[4];
          
        int n = 3;
        while (n >= 0) {
        	if(a%10 == b%10) {
        		result[n] = a%10;
        	}
        	else {
        		result[n] = -1;
        	}
        	a = a/10;
        	b = b/10;
        	n = n -1;
        }

        return result;

	}

    public boolean elminateIfIndexesDontMatch(int[] expected, int a) {

    	int n = 3;
    	while(n >= 0) {
    		if(expected[n] >= 0) {
    			if(a%10 != expected[n]) {
    				return false;
    			}
    		}
    		a = a/10;
    		n = n - 1;
    	}
    	return true;
    }

    public ArrayList<Integer> whichIsBestStartingNumber () {

    	ArrayList<Integer> r = new ArrayList<Integer>();

    	int currentMaxElim = -1;
    	for(int i = 0; i < list.size(); i++) {
    		int temp = numElim(list.get(i));
    		if(temp > currentMaxElim) {
    			currentMaxElim = temp;
    			r = new ArrayList<Integer>();
    			r.add(list.get(i));
    		}
    		else if (temp == currentMaxElim) {
    			r.add(list.get(i));
    		}

    	}

    	System.out.println("maxMin elim value is: " + currentMaxElim);
    	System.out.println("size of list is " + list.size());
    	System.out.println("size of r is" + r.size());
    	return r;

    }

    public void eliminateSomePossibilities(int guess, int nmatches) {
    	for (int i = list.size() - 1; i >=0; i--) {
			
			/* for each element in the arraylist, if the number of matching digits between that element
			and the most recent guess does not equal nmatches (the number of matching digits between the 
			most recent guess and the secret answer), remove that element */
			if(numMatches(guess, (int) list.get(i)) != nmatches) {
				list.remove(i);
			}
		}
    }

    public int numElim(int k) {
    	int min = Integer.MAX_VALUE;

    	for(int i = 1; i<4; i++) {
    		int count = 0;
    		for (int j = 0; j < list.size(); j++) {
    			if(numMatches(k, list.get(j)) != i) {
    				count++;
    			}
    		}

    		min = Math.min(min, count);
    	}
    	return min;
    }


	private ArrayList<Integer> list;
	private int numGuesses;
	private Random r;
	// fill in code here
	// common data structures and variables

	public numMatch() {
		// fill in code here
		// initialization

		list = new ArrayList<Integer>();

		for(int i = 1000; i < 10000; i++) {
			list.add(i);
		}

		r = new Random();



	}

	public int getList() {
		int g =  r.nextInt(list.size());
		return (int) list.get(g);
	}

	public static void main(String[] args) {

		/*System.out.println(numMatches(1535, 1235));

		numMatch nm = new numMatch();
		int[] t = new int[4];
		t[0] = -1;
		t[1] = 1;
		t[2] = -1;
		t[3] = 5;

		int test = 3135;
		System.out.println(nm.elminateIfIndexesDontMatch(t, test));*/

		numMatch nm = new numMatch();
		nm.eliminateSomePossibilities(5566, 0);
		nm.eliminateSomePossibilities(1731, 2);


		System.out.println(nm.whichIsBestStartingNumber());
	}

}